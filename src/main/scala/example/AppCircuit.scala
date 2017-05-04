package example

import diode._
import diode.data.Pot
import diode.react.ReactConnector

/**
  * AppCircuit provides the actual instance of the `AppModel` and all the action
  * handlers we need. Everything else comes from the `Circuit`
  */
object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // define initial value for the application model
  override def initialModel: RootModel = RootModel(Pot.empty)

  override val actionHandler = composeHandlers(
    new NetstatHandler(zoomTo(_.entries))
  )
}

