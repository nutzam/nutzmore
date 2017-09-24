package org.nutz.integration.jedisque;

import com.github.xetorthio.jedisque.Jedisque;
import com.github.xetorthio.jedisque.Job;
import com.github.xetorthio.jedisque.JobInfo;
import com.github.xetorthio.jedisque.JobParams;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Jianghao on 2017/9/24
 *
 * @howechiang
 */
public class DisqueService extends Jedisque {

    protected JedisqueAgent jedisqueAgent;

    protected Jedisque getJedisque() {
        try {
            return jedisqueAgent.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String addJob(String queueName, String job, long mstimeout) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.addJob(queueName, job, mstimeout);
        } finally {
            jedisque.close();
        }
    }

    public String addJob(String queueName, String job, long mstimeout, JobParams params) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.addJob(queueName, job, mstimeout, params);
        } finally {
            jedisque.close();
        }
    }

    public List<Job> getJob(String... queueNames) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.getJob(queueNames);
        } finally {
            jedisque.close();
        }
    }

    public List<Job> getJob(long timeout, long count, String... queueNames) {
        Jedisque jedisque = getJedisque();
        try {
            return getJob(timeout, count, queueNames);
        } finally {
            jedisque.close();
        }
    }

    public Long working(String jobId) {
        Jedisque jedisque = getJedisque();
        try {
            return working(jobId);
        } finally {
            jedisque.close();
        }
    }

    public String ping() {
        Jedisque jedisque = getJedisque();
        try {
            return ping();
        } finally {
            jedisque.close();
        }
    }

    public JobInfo show(String jobId) {
        Jedisque jedisque = getJedisque();
        try {
            return show(jobId);
        } finally {
            jedisque.close();
        }
    }

    public Long fastack(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return fastack(jobIds);
        } finally {
            jedisque.close();
        }
    }

    public Long enqueue(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return enqueue(jobIds);
        } finally {
            jedisque.close();
        }
    }

    public Long dequeue(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return dequeue(jobIds);
        } finally {
            jedisque.close();
        }
    }

    public Long delJob(String jobId) {
        Jedisque jedisque = getJedisque();
        try {
            return delJob(jobId);
        } finally {
            jedisque.close();
        }
    }

    public List<Job> qpeek(String queueName, long count) {
        Jedisque jedisque = getJedisque();
        try {
            return qpeek(queueName, count);
        } finally {
            jedisque.close();
        }
    }

    public Long qlen(String queueName) {
        Jedisque jedisque = getJedisque();
        try {
            return qlen(queueName);
        } finally {
            jedisque.close();
        }
    }

    public String info(String section) {
        Jedisque jedisque = getJedisque();
        try {
            return info(section);
        } finally {
            jedisque.close();
        }
    }

    public String info() {
        Jedisque jedisque = getJedisque();
        try {
            return info();
        } finally {
            jedisque.close();
        }
    }

    public Long ackjob(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return ackjob(jobIds);
        } finally {
            jedisque.close();
        }
    }
}
