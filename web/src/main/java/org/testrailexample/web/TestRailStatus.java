package org.testrailexample.web;

public enum TestRailStatus {
  PASSED(1),
  BLOCKED(2),
  UNTESTED(3),
  RETEST(4),
  FAILED(5);



  private int id;
  public int getId() {
    return id;
  }

  TestRailStatus(int id) {
    this.id = id;
  }



}
