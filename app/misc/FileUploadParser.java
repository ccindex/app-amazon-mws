package misc;

import models.QueueError;
import models.QueueMaster;

import java.util.ArrayList;
import java.util.List;


public class FileUploadParser {

  public static enum Action {
    ITEM, ITEM_BASIC, ITEM_LOGISTICS, ITEM_CUSTOMS,
    ITEM_BRAND, ITEM_DESCRIPTION, ITEM_IMAGES, ITEM_INSTRUCTIONS,ITEM_CERTIFICATION,

    PURCHASE_REQUEST,PURCHASE_VENDOR,

    ORDER_IMPORT
  }

  public static Boolean parse(QueueMaster qm) {
    ParseResult result = null;

    qm.status = QueueMaster.Status.RUNNING;
    qm.save();

    qm.save();

    return true;
  }

	static class ParseResult {
		public int total;
		public int pending = 0;
		public int succeed = 0;
		public int rejected = 0;
		public int failed = 0;

		public List<QueueError> errors = new ArrayList<QueueError>();
	}
}
