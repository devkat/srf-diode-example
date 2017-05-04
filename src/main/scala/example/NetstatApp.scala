package example

import boopickle.Default._
import diode.dev.{Hooks, PersistStateIDB}
import example.view.RecordList
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray._

@JSExportTopLevel("NetstatApp")
object NetstatApp extends JSApp {

  val baseUrl = BaseUrl(dom.window.location.href.takeWhile(_ != '#'))

  val routerConfig: RouterConfig[RecordFilter] = RouterConfigDsl[RecordFilter].buildConfig { dsl =>
    import dsl._

    val netstatConnection = AppCircuit.connect(_.entries)

    /* how the application renders the list given a filter */
    def filterRoute(s: RecordFilter): Rule = staticRoute("#/" + s.link, s) ~> renderR(router => netstatConnection(p => RecordList(p, s, router)))

    val filterRoutes: Rule = RecordFilter.values.map(filterRoute).reduce(_ | _)

    /* build a final RouterConfig with a default page */
    filterRoutes.notFound(redirectToPage(RecordFilter.All)(Redirect.Replace))
  }

  /** The router is itself a React component, which at this point is not mounted (U-suffix) */
  val router: ReactComponentU[Unit, Resolution[RecordFilter], Any, TopNode] =
    Router(baseUrl, routerConfig.logToConsole)()

  /**
    * Function to pickle application model into a TypedArray
    *
    * @param model
    * @return
    */
  def pickle(model: NetstatModel) = {
    val data = Pickle.intoBytes(model)
    data.typedArray().subarray(data.position, data.limit)
  }

  /**
    * Function to unpickle application model from a TypedArray
    *
    * @param data
    * @return
    */
  def unpickle(data: Int8Array) = {
    Unpickle[NetstatModel].fromBytes(TypedArrayBuffer.wrap(data))
  }

  @JSExport
  override def main(): Unit = {
    // add a development tool to persist application state
    AppCircuit.addProcessor(new PersistStateIDB(pickle, unpickle))
    // hook it into Ctrl+Shift+S and Ctrl+Shift+L
    Hooks.hookPersistState("test", AppCircuit)

    AppCircuit.dispatch(LoadNetstatModel)
    ReactDOM.render(router, dom.document.getElementsByClassName("todoapp")(0))
  }
}
