package amazon.models.report;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import play.data.format.Formats;
import play.db.ebean.Model;

/**
 * Title: AmazonReportOrderSnapshot.java
 *
 * @author Lc
 *
 * @date 2016年4月21日 下午3:22:38
 */
@Entity
public class AmazonReportOrderSnapshot extends Model {
	
	@Id
	public Long id;
	
	public String amazonOrderId;     // [250-6624888-8317432]
	
	public String merchantOrderId;   // [250-6624888-8317432]
	
	public String purchaseDate;      // [2016-04-17T19:29:04+09:00]
	
	public String lastUpdatedDate;   // [2016-04-19T04:16:47+09:00]
	
	public String orderStatus;       // [Pending]
	
	public String fulfillmentChannel;// [Amazon]
	
	public String salesChannel;      // [Amazon.co.jp]
	
	public String orderChannel;      // []
	
	public String url;               // []
	
	public String shipServiceLevel;  // [Standard]
	
	@Column(length=2000)
	public String productName;       // [iClever 2.4GHzミニワイヤレスキーボード(IC-RF01)　タッチパッドを搭載　超小型 キーボード 無線 USB レシーバー　min]
	
	public String sku;               // [132-66001-04-001]
	
	public String asin;              // [B00HMXIKCS]
	
	public String itemStatus;        // [Unshipped]
	
	public String quantity;          // [1]
	
	public String currency;          // [JPY]
	
	public String itemPrice;         // [2699.0]
	
	public String itemTax;           // []
	
	public String shippingPrice;     // []
	
	public String shippingTax;       // []
	
	public String giftWrapPrice;     // []
	
	public String giftWrapTax;       // []
	
	public String itemPromotionDiscount; // []
	
	public String shipPromotionDiscount; // []
	
	public String shipCity;              // []
	
	public String shipState;             // [広島県]
	
	public String shipPostalCode;       // [731-5144]
	
	public String shipCountry;          // [JP]
	
	public String promotionIds;         // [JP Core Free Shipping  2016/03/29 6-38-40-593][]
	
	public String isBusinessOrder;
	
	public String purchaseOrderNumber;
	
	public String priceDesignation;
	
	@Column(nullable=false)
	public Long reportId;
	
	public boolean isHandle = false;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date createdAt = new Date();
	
	@Version
	public Date updatedAt;

}
