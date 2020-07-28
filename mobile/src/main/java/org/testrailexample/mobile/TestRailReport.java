package org.testrailexample.mobile;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Plan;
import com.codepine.api.testrail.model.Plan.Entry;
import com.codepine.api.testrail.model.Project;
import com.codepine.api.testrail.model.Result;
import com.codepine.api.testrail.model.ResultField;
import com.google.common.collect.Lists;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestRailReport {
  private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  private static final List<Result> results = new ArrayList<>();

  private static final String url = "{YOUR_TESTRAIL_URL_HERE}"; //This should be in properties
  private static final String userId = "{YOUR_EMAIL_HERE}";//This should be in properties
  private static final String pwd = "{YOUR_PASSWORD_HERE}";//This should be in properties
  private static final String testPlanSuffix = "{WHAT EVER TEST PLAN SUFFIX YOU WANT TO PUT}";//This should be in properties
  private static final int projectId = 3; // TestRail projectId. this is constant value. This should be in properties
  private static final int suiteId = 39; // TestRail suiId. this is constant value. This should be in properties

  public static void addResult(Result result){
    results.add(result);
  }

  public static void reportResults() {
    String testDriver = System.getProperty("testDriver");

    TestRail testRail = TestRail.builder(url, userId, pwd).build();

    // Get testRail project
    Project project = testRail.projects().get(projectId).execute();
    Plan plan = TestRailReport.loadTestPlanId(testRail, projectId, testPlanSuffix);

    int currentRunId = TestRailReport.loadCurrentRunId(testRail, plan, projectId, testDriver);
    List<ResultField> customResultFields = testRail.resultFields().list().execute();
    testRail.results().addForCases(currentRunId, results, customResultFields).execute();

    try {
      Files.deleteIfExists(TestRailReport.runIdFilePath());
    } catch (Exception ignored) { }
  }

  private static Plan loadTestPlanId(TestRail testRail, int projectId, String testPlanSuffix) {
    Path testPlanIdSavedFile = TestRailReport.planIdFilePath();

    if(Files.exists(testPlanIdSavedFile)) {
      try {
        String testPLanIdRaw = Files.readAllLines(testPlanIdSavedFile).get(0);

        return testRail.plans().get(Integer.parseInt(testPLanIdRaw)).execute();
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    } else {
      Plan plan = new Plan();
      plan.setName(String.format("Plan - '%s' - '%s'", SIMPLE_DATE_FORMAT.format(new Date()), testPlanSuffix));
      plan.setProjectId(projectId);

      Plan createdPlan = testRail.plans().add(projectId, plan).execute();
      createdPlan.setEntries(new ArrayList<>());
      try {
        Files.write(testPlanIdSavedFile, String.valueOf(createdPlan.getId()).getBytes());
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }

      return createdPlan;
    }
  }

  private static Path planIdFilePath() {
    Path projectRoot = FileSystems.getDefault().getPath("").toAbsolutePath();
    System.out.println("Present Project Directory : "+ projectRoot.toAbsolutePath().toString());
    return Paths.get(projectRoot.getParent().toAbsolutePath().toString(),"testPlanId");
  }

  private static Path runIdFilePath() {
    Path projectRoot = FileSystems.getDefault().getPath("").toAbsolutePath();
    System.out.println("Present Project Directory : "+ projectRoot.toAbsolutePath().toString());
    return Paths.get(projectRoot.toAbsolutePath().toString(),"runPlanId");
  }

  private static int loadCurrentRunId(TestRail testRail, Plan plan, int projectId, String runNameSuffix) {
    Entry.Run run1 = new Entry.Run();
    run1.setIncludeAll(false)
      .setSuiteId(suiteId)
      .setCaseIds(
        results.stream()
          .map(Result::getCaseId)
          .collect(Collectors.toList())
      );

    String browserVersionRandom = Math.random() < 0.5 ? "ANDROID" : "iOS";

    Entry runEntry = new Entry()
      .setName(String.format("Run - '%s' - '%s'", SIMPLE_DATE_FORMAT.format(new Date()), browserVersionRandom))
      .setSuiteId(suiteId)
      .setIncludeAll(false)
      .setCaseIds(
        results.stream()
          .map(Result::getCaseId)
          .collect(Collectors.toList())
      ).setRuns(Lists.newArrayList(run1));

    Plan retrievedPlan = testRail.plans().get(plan.getId()).execute();
    Entry thisRunEntry = testRail.plans().addEntry(retrievedPlan.getId(), runEntry).execute();

    Path runIdPath = TestRailReport.runIdFilePath();
    if(Files.exists(runIdPath)) {
      try {
        return Integer.parseInt(Files.readAllLines(runIdPath).get(0));
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    } else {
      if(Optional.ofNullable(thisRunEntry).isPresent() && Objects.nonNull(thisRunEntry.getId())) {
        try {
          Files.write(runIdPath, String.valueOf(thisRunEntry.getRuns().get(0).getId()).getBytes());
          return thisRunEntry.getRuns().get(0).getId();
        } catch (Exception exception) {
          throw new RuntimeException(exception);
        }
      } else {
        throw new RuntimeException("Invalid state. Run Id must exist in this phase");
      }
    }
  }
}
