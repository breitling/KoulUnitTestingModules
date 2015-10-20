package org.breitling.dragon.framework.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    org.breitling.dragon.framework.test.TestAssertions.class,
    org.breitling.dragon.framework.test.TestClassInit.class,
    org.breitling.dragon.framework.test.TestDb.class,
    org.breitling.dragon.framework.test.TestExceptions.class,
    org.breitling.dragon.framework.test.TestFunctionSupport.class,
    org.breitling.dragon.framework.test.TestMethodInit.class,
    org.breitling.dragon.framework.test.TestMultipleDatasets.class,
    org.breitling.dragon.framework.test.TestQualifiedNames.class,
    org.breitling.dragon.framework.test.TestSimple.class,
    org.breitling.dragon.framework.test.TestUtilities.class,
    org.breitling.dragon.framework.test.TestValidation.class,
    org.breitling.dragon.framework.test.TestXmlDataSet.class
})

public class AllTests
{
    // ALL THE TESTS...
}
