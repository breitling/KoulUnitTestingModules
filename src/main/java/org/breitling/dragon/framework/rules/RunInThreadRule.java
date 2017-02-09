package org.breitling.dragon.framework.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.breitling.dragon.framework.annotations.RunInThread;

/**
 * Contributed by Frank Appel from GitHub.
 * 
 * @author fabbel
 *
 * (https://gist.github.com/fappel)
 */

public class RunInThreadRule implements TestRule
{
//	@Override
	public Statement apply(Statement base, Description description) 
	{
		Statement result = base;
		RunInThread annotation = description.getAnnotation(RunInThread.class);
		
		if (annotation != null)
			result = new RunInThreadStatement(base);

		return result;
	}
}