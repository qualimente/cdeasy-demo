package pipeline.jobs

import com.camiloribeiro.cdeasy.job.JobHelper
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.views.DeliveryPipelineView

def productName = "FooBar"

def jobNames = [
    compile_and_unit_test: "${productName}-compile_and_unit_test"
    , integration_test : "${productName}-integration_test"
    , sonar_quality_gate : "${productName}-sonar_quality_gate"
    , deploy_to_stage : "${productName}-deploy_to_stage"
    , api_tests_stage : "${productName}-api_tests_stage"
    , e2e_tests_stage : "${productName}-e2e_tests_stage"
]

Job compile_and_unit_test = JobHelper.createJob(this as DslFactory, jobNames.compile_and_unit_test)
JobHelper.addStep(compile_and_unit_test, "sleep \$((RANDOM%10+5))")
JobHelper.addDeliveryPipelineConfiguration(compile_and_unit_test, 'Build', 'Compile & Unit Test')
JobHelper.addDownstreamParameterized(compile_and_unit_test, [jobNames.integration_test], "SUCCESS")

Job integration_test = JobHelper.createJob(this as DslFactory, jobNames.integration_test)
JobHelper.addStep(compile_and_unit_test, "sleep \$((RANDOM%10+5))")
JobHelper.addDeliveryPipelineConfiguration(integration_test, 'Build', 'Integration Tests')
JobHelper.addDownstreamParameterized(integration_test, [jobNames.sonar_quality_gate], "SUCCESS")

Job sonar_quality_gate = JobHelper.createJob(this as DslFactory, jobNames.sonar_quality_gate)
JobHelper.addStep(sonar_quality_gate, "sleep \$((RANDOM%10+5))")
JobHelper.addDeliveryPipelineConfiguration(sonar_quality_gate, 'Build', 'Sonar Code Quality Gate')

Job deploy_to_stage = JobHelper.createJob(this as DslFactory, jobNames.deploy_to_stage)
JobHelper.addStep(deploy_to_stage, "sleep \$((RANDOM%10+5))")
JobHelper.addDownstreamParameterized(deploy_to_stage, [jobNames.api_tests_stage, jobNames.e2e_tests_stage], "SUCCESS")
JobHelper.addDeliveryPipelineConfiguration(deploy_to_stage, 'Test', 'Deploy to Stage')

Job api_tests_stage = JobHelper.createJob(this as DslFactory, jobNames.api_tests_stage)
JobHelper.addStep(api_tests_stage, "sleep \$((RANDOM%10+5))")
JobHelper.addDeliveryPipelineConfiguration(api_tests_stage, 'Test', 'API Tests - Stage')

Job e2e_tests_stage = JobHelper.createJob(this as DslFactory, jobNames.e2e_tests_stage)
JobHelper.addStep(e2e_tests_stage, "sleep \$((RANDOM%10+5))")
JobHelper.addDeliveryPipelineConfiguration(e2e_tests_stage, 'Test', 'e2e Functional Tests - Stage')

def pipelineName = "${productName} Delivery Pipeline"
deliveryPipelineView(pipelineName) {
  pipelineInstances(5)
    columns(1)
    sorting(DeliveryPipelineView.Sorting.LAST_ACTIVITY)
    updateInterval(10)
    enableManualTriggers(true)
    showAvatars()
    showChangeLog()
    pipelines {
      component(pipelineName, jobNames.compile_and_unit_test)
    }
}
