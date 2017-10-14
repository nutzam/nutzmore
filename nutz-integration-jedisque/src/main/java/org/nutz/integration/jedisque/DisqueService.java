package org.nutz.integration.jedisque;

import com.github.xetorthio.jedisque.Jedisque;
import com.github.xetorthio.jedisque.Job;
import com.github.xetorthio.jedisque.JobInfo;
import com.github.xetorthio.jedisque.JobParams;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Jianghao on 2017-09-24
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
            return jedisque.getJob(timeout, count, queueNames);
        } finally {
            jedisque.close();
        }
    }

    public Long working(String jobId) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.working(jobId);
        } finally {
            jedisque.close();
        }
    }

    public String ping() {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.ping();
        } finally {
            jedisque.close();
        }
    }

    public JobInfo show(String jobId) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.show(jobId);
        } finally {
            jedisque.close();
        }
    }

    public Long fastack(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.fastack(jobIds);
        } finally {
            jedisque.close();
        }
    }

    public Long enqueue(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.enqueue(jobIds);
        } finally {
            jedisque.close();
        }
    }

    public Long dequeue(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.dequeue(jobIds);
        } finally {
            jedisque.close();
        }
    }

    public Long delJob(String jobId) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.delJob(jobId);
        } finally {
            jedisque.close();
        }
    }

    public List<Job> qpeek(String queueName, long count) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.qpeek(queueName, count);
        } finally {
            jedisque.close();
        }
    }

    public Long qlen(String queueName) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.qlen(queueName);
        } finally {
            jedisque.close();
        }
    }

    public String info(String section) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.info(section);
        } finally {
            jedisque.close();
        }
    }

    public String info() {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.info();
        } finally {
            jedisque.close();
        }
    }

    public Long ackjob(String... jobIds) {
        Jedisque jedisque = getJedisque();
        try {
            return jedisque.ackjob(jobIds);
        } finally {
            jedisque.close();
        }
    }
}
