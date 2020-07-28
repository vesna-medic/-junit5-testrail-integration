package org.testrailexample.mobile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestRailIdProvider.class)
public class MobileUserLoginTest {
  @Test
  @TestRailCase(id="288")
  public void test1(){
    Assertions.assertTrue((Math.random() < 0.5));
  }

  @Test
  @TestRailCase(id="289")
  public void test2(){
    Assertions.assertTrue((Math.random() < 0.5));
  }

  @Test
  @TestRailCase(id="290")
  public void test3(){
    Assertions.assertTrue((Math.random() < 0.5));
  }
}
