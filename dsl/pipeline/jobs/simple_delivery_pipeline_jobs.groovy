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
