package example

import diode._
import diode.react.ReactConnector

/**
  * AppCircuit provides the actual instance of the `AppModel` and all the action
  * handlers we need. Everything else comes from the `Circuit`
  */
object AppCircuit extends Circuit[NetstatModel] with ReactConnector[NetstatModel] {
  // define initial value for the application model
  def initialModel = NetstatModel(Nil)

  override val actionHandler = composeHandlers(
    new NetstatHandler(zoomTo(_.entries))
  )
}

