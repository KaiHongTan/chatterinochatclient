package com.hep88.view
import akka.actor.typed.ActorRef
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, ListView, TextField}
import com.hep88.ChatClient
import com.hep88.User
import com.hep88.Client
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
@sfxml
class MainWindowController(private val txtName: TextField,
private val lblStatus: Label, private val listUser: ListView[User],
private val listMessage: ListView[String],
private val txtMessage: TextField) {

    var chatClientRef: Option[ActorRef[ChatClient.Command]] = None

    val receivedText: ObservableBuffer[String] =  new ObservableBuffer[String]()

    listMessage.items = receivedText

    def handleJoin(action: ActionEvent): Unit = {
        if(txtName != null)
          chatClientRef map (_ ! ChatClient.StartJoin(txtName.text()))
    }

    def displayStatus(text: String): Unit = {
        lblStatus.text = text
    }
  def updateList(x: Iterable[User]): Unit ={
    listUser.items = new ObservableBuffer[User]() ++= x
  }
  def handleSend(actionEvent: ActionEvent): Unit ={

    if (listUser.selectionModel().selectedIndex.value >= 0){
      val selectedUser = listUser.selectionModel().selectedItem.value
      Client.userRef ! ChatClient.SendMessageL(selectedUser.ref,
        txtMessage.text(), txtName.text())
      addText(txtMessage.text(), "You", isGlobal = false)
    }
  }
  def handleSendGlobal(actionEvent: ActionEvent): Unit = {
    chatClientRef map (_ ! ChatClient.SendGlobalMessage(txtMessage.text(), txtName.text()))
    addText(txtMessage.text(), "You", isGlobal = true)
  }
  def addText(text: String, sender:String, isGlobal:Boolean): Unit = {
    if(isGlobal) receivedText += sender +" (Global): "+ text
    if(!isGlobal) receivedText += sender +": "+ text

  }

}