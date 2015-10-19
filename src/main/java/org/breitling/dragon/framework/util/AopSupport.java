package org.breitling.dragon.framework.util;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class AopSupport
{
	public static Object unwrapProxy(Object bean) throws Exception
	{
		if (AopUtils.isAopProxy(bean) && bean instanceof Advised) 
		{
			Advised advised = (Advised) bean;
			bean = advised.getTargetSource().getTarget();
		}

		return bean;
	}
}
