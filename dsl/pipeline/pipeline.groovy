package pipeline

import javaposse.jobdsl.dsl.views.DeliveryPipelineView

def pipelineName = 'Product Pipeline Example'
deliveryPipelineView(pipelineName) {
  pipelineInstances(5)
    columns(1)
    sorting(DeliveryPipelineView.Sorting.LAST_ACTIVITY)
    updateInterval(10)
    enableManualTriggers(true)
    showAvatars()
    showChangeLog()
    pipelines {
      component(pipelineName, 'unit_test')
    }
}
