package actors;

import akka.actor.UntypedActor;
import misc.FileUploadParser;
import models.QueueMaster;

public class FileUploadActor extends UntypedActor {

  public static enum Msg {
    START, DONE;
  }

  @Override
  public void onReceive(Object message) throws Exception {
    if (message instanceof QueueMaster) {
      getSender().tell(Msg.START, getSelf());
      Boolean result = FileUploadParser.parse((QueueMaster) message);
    } else
      unhandled(message);
  }
}
