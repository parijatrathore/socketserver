package com.socketserver.server;

public class LockingAspect {

	@Around("execution(* *(..)) && @annotation(locksAnnotation)")
	public Object executeAfterLock(ProceedingJoinPoint pjp, Locks locksAnnotation) throws Throwable {

	}

}
