package org.testrailexample.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestRailIdProvider.class)
public class WebUserLoginTest {
  @Test
  @TestRailCase(id="280")
  public void test1(){
    assertTrue((Math.random() < 0.5));
  }

  @Test
  @TestRailCase(id="281")
  public void test2(){
    assertTrue((Math.random() < 0.5));
  }

  @Test
  @TestRailCase(id="282")
  public void test3(){
    assertTrue((Math.random() < 0.5));
  }
}
