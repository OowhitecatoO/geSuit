package net.cubespace.geSuit.core.channel;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import net.cubespace.geSuit.core.ConnectionNotifier;
import net.cubespace.geSuit.core.channel.RedisChannelManager.PubSubHandler;

import com.google.common.base.Charsets;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisConnection {
    private JedisPool pool;
    private boolean connectionActive;
    private ConnectionNotifier notifier;

    private long connectionKey;

    public RedisConnection(String host, int port, String password) throws IOException {
        connectionKey = System.nanoTime();

        connectionActive = false;

        if (password == null || password.isEmpty())
            pool = new JedisPool(new JedisPoolConfig(), host, port, 0);
        else
            pool = new JedisPool(new JedisPoolConfig(), host, port, 0, password);

        // Test the connection
        JedisRunner<Void> runner = new JedisRunner<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.ping();
                return null;
            }
        };

        if (!(connectionActive = runner.run()))
            throw new IOException(runner.getLastError());
    }

    public void setNotifier(ConnectionNotifier notifier) {
        this.notifier = notifier;
    }

    public void shutdown() {
        pool.destroy();
    }

    public long getKey() {
        return connectionKey;
    }

    public void testConnection() {
        JedisRunner<Void> runner = new JedisRunner<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.ping();
                return null;
            }
        };

        if (runner.run())
            markActive();
        else
            markInactive(runner.getLastError());
    }

    private void markActive() {
        if (connectionActive || notifier == null)
            return;

        connectionActive = true;
        notifier.onConnectionRestored();
    }

    private void markInactive(Throwable e) {
        if (!connectionActive || notifier == null)
            return;

        connectionActive = false;
        notifier.onConnectionLost(e);
    }

    /**
     * NOTE: This must be called from a thread as it will block until the
     * subscription is terminated
     */
    public void subscribe(final byte[] name, final PubSubHandler pubsub) {
        boolean end = false;
        JedisRunner<Void> runner = new JedisRunner<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.psubscribe(pubsub, name);
                return null;
            }
        };

        while (!end)
            end = runner.run();
    }

    public void publish(final byte[] name, final byte[] data) {
        new JedisRunner<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.publish(name, data);
                return null;
            }
        }.run();
    }

    public byte[] getValue(final byte[] name) {
        JedisRunner<byte[]> runner = new JedisRunner<byte[]>() {
            @Override
            public byte[] execute(Jedis jedis) {
                return jedis.get(name);
            }
        };

        if (runner.run())
            return runner.getReturnedValue();
        runner.getLastError().printStackTrace();
        return null;
    }

    public Set<byte[]> getKeys(final byte[] pattern) {
        JedisRunner<Set<byte[]>> runner = new JedisRunner<Set<byte[]>>() {
            @Override
            public Set<byte[]> execute(Jedis jedis) {
                return jedis.keys(pattern);
            }
        };

        if (runner.run())
            return runner.getReturnedValue();

        runner.getLastError().printStackTrace();
        return Collections.emptySet();
    }

    public void setValue(final byte[] name, final byte[] value) {
        new JedisRunner<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.set(name, value);
                return null;
            }
        }.run();
    }

    public void removeValue(final byte[] name) {
        new JedisRunner<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.del(name);
                return null;
            }
        }.run();
    }

    private byte[] mRemoveScriptSHA1 = null;

    public void removePattern(final byte[] pattern) {
        new JedisRunner<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                if (mRemoveScriptSHA1 == null) {
                    String script = "local keys = redis.call('keys', ARGV[1]) \n for i=1,#keys,5000 do \n redis.call('del', unpack(keys, i, math.min(i+4999, #keys))) \n end";
                    mRemoveScriptSHA1 = jedis.scriptLoad(script.getBytes(Charsets.UTF_8));
                }

                jedis.evalsha(mRemoveScriptSHA1, 0, pattern);
                return null;
            }
        }.run();
    }

    public abstract class JedisRunner<ReturnValue> {
        private Throwable mLastError;
        private ReturnValue mValue;

        public final boolean run() {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                mValue = execute(jedis);
                markActive();
                return true;
            } catch (JedisConnectionException e) {
                mLastError = e;
                pool.returnBrokenResource(jedis);
                markInactive(e);
                jedis = null;
                return false;
            } catch (Exception e) {
                mLastError = e;
                return false;
            } finally {
                pool.returnResource(jedis);
            }
        }

        public final boolean runAndThrow() {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                mValue = execute(jedis);
                markActive();
                return true;
            } catch (JedisConnectionException e) {
                mLastError = e;
                pool.returnBrokenResource(jedis);
                markInactive(e);
                jedis = null;
                return false;
            } catch (Exception e) {
                mLastError = e;
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                else
                    throw new RuntimeException(e);
            } finally {
                pool.returnResource(jedis);
            }
        }

        public final Throwable getLastError() {
            return mLastError;
        }

        public final ReturnValue getReturnedValue() {
            return mValue;
        }

        public abstract ReturnValue execute(Jedis jedis) throws Exception;
    }
}
