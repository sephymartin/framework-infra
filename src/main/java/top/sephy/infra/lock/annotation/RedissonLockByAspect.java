/*
 * Copyright 2022-2024 sephy.top
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.sephy.infra.lock.annotation;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import lombok.extern.slf4j.Slf4j;
import top.sephy.infra.exception.SystemException;
import top.sephy.infra.utils.SpELUtils;

@Slf4j
@Aspect
public class RedissonLockByAspect {

    private final RedissonClient redissonClient;

    public RedissonLockByAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Pointcut("@annotation(top.sephy.infra.lock.annotation.RedisLock)")
    public void redisLock() {}

    @Around("redisLock()")
    public Object doWithLock(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);

        String lockKey =
            redisLock.keyType() == KeyType.DEFAULT ? redisLock.key() : SpELUtils.parse(redisLock.key(), joinPoint);

        LockMode lockType = redisLock.lockType();

        RLock lock = redissonClient.getLock(lockKey);
        if (lockType == LockMode.LOCK) {
            return proceedWithLock(joinPoint, lock, redisLock);
        } else if (lockType == LockMode.LOCK_INTERRUPTIBLY) {
            return proceedWithLockInterruptibly(joinPoint, lock, redisLock);
        } else if (lockType == LockMode.TRYLOCK) {
            return proceedTryLock(joinPoint, lock, redisLock);
        }
        return joinPoint.proceed();
    }

    /**
     * 使用 lock() 方法获取锁
     * 
     * @param joinPoint
     * @param lock
     * @param redisLock
     * @return
     */
    private Object proceedWithLock(ProceedingJoinPoint joinPoint, RLock lock, RedisLock redisLock) {
        long leaseTime = redisLock.leaseTime();
        if (leaseTime > 0) {
            lock.lock(leaseTime, redisLock.timeUnit());
        } else {
            lock.lock();
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new SystemException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用 lockInterruptibly() 方法获取锁
     *
     * @param joinPoint
     * @param lock
     * @param redisLock
     * @return
     */
    private Object proceedWithLockInterruptibly(ProceedingJoinPoint joinPoint, RLock lock, RedisLock redisLock)
        throws InterruptedException {
        long leaseTime = redisLock.leaseTime();
        if (leaseTime > 0) {
            lock.lockInterruptibly(leaseTime, redisLock.timeUnit());
        } else {
            lock.lockInterruptibly();
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new SystemException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用 tryLock() 方法获取锁
     * 
     * @param joinPoint
     * @param lock
     * @param redisLock
     * @return
     */
    private Object proceedTryLock(ProceedingJoinPoint joinPoint, RLock lock, RedisLock redisLock)
        throws InterruptedException {
        long waitTime = redisLock.lockWaitTime();
        long leaseTime = redisLock.leaseTime();
        boolean locked = false;
        if (waitTime > 0) {
            if (leaseTime > 0) {
                locked = lock.tryLock(waitTime, leaseTime, redisLock.timeUnit());
            } else {
                locked = lock.tryLock(waitTime, redisLock.timeUnit());
            }
        } else {
            locked = lock.tryLock();
        }

        if (!locked) {
            if (redisLock.failBehavior() == FailBehavior.FAIL_FAST) {
                throw new SystemException("获取锁失败");
            } else {
                return null;
            }
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new SystemException(e);
        } finally {
            lock.unlock();
        }
    }
}
