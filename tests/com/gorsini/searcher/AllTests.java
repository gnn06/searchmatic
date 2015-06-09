package com.gorsini.searcher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		MovieSelector411Test.class,
		MovieSelectorTest.class, 
		SearcherT411Test.class})
public class AllTests {

}
