# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table amazon_order (
  id                        bigint auto_increment not null,
  amazon_order_id           varchar(255) not null,
  seller_order_id           varchar(255),
  purchase_date             varchar(255),
  last_update_date          varchar(255) not null,
  order_status              varchar(255),
  fulfillment_channel       varchar(255),
  sales_channel             varchar(255),
  order_channel             varchar(255),
  ship_service_level        varchar(255),
  name                      varchar(255),
  address_line1             varchar(255),
  address_line2             varchar(255),
  address_line3             varchar(255),
  city                      varchar(255),
  county                    varchar(255),
  district                  varchar(255),
  state_or_region           varchar(255),
  postal_code               varchar(255),
  country_code              varchar(255),
  phone                     varchar(255),
  currency_code             varchar(255),
  amount                    varchar(255),
  number_of_items_shipped   varchar(255),
  number_of_items_unshipped varchar(255),
  payment_execution_detail  varchar(255),
  payment_method            varchar(255),
  marketplace_id            varchar(255),
  buyer_email               varchar(255),
  buyer_name                varchar(255),
  shipment_service_level_category varchar(255),
  shipped_by_amazon_tfm     varchar(255),
  tfm_shipment_status       varchar(255),
  cba_displayable_shipping_label varchar(255),
  order_type                varchar(255),
  earliest_ship_date        varchar(255),
  latest_ship_date          varchar(255),
  earliest_delivery_date    varchar(255),
  latest_delivery_date      varchar(255),
  is_business_order         varchar(255),
  is_premium_order          varchar(255),
  is_prime                  varchar(255),
  mws_response              varchar(2000) not null,
  app_name                  varchar(255) not null,
  status                    integer,
  is_handle                 tinyint(1) default 0,
  created_at                datetime,
  updated_at                datetime not null,
  constraint ck_amazon_order_status check (status in (0,1,2)),
  constraint pk_amazon_order primary key (id))
;

create table amazon_order_item (
  id                        bigint auto_increment not null,
  asin                      varchar(255),
  seller_sku                varchar(255),
  order_item_id             varchar(255),
  title                     varchar(2000),
  quantity_ordered          varchar(255),
  quantity_shipped          varchar(255),
  item_price_currency_code  varchar(255),
  item_price_amount         varchar(255),
  shipping_price_currency_code varchar(255),
  shipping_price_amount     varchar(255),
  gift_wrap_price_currency_code varchar(255),
  gift_wrap_price_amount    varchar(255),
  item_tax_currency_code    varchar(255),
  item_tax_amount           varchar(255),
  shipping_tax_currency_code varchar(255),
  shipping_tax_amount       varchar(255),
  gift_wrap_tax_currency_code varchar(255),
  gift_wrap_tax_amount      varchar(255),
  shipping_discount_currency_code varchar(255),
  shipping_discount_amount  varchar(255),
  promotion_discount_currency_code varchar(255),
  promotion_discount_amount varchar(255),
  promotion_ids             varchar(255),
  cod_fee_currency_code     varchar(255),
  cod_fee_amount            varchar(255),
  cod_fee_discount_currency_code varchar(255),
  cod_fee_discount_amount   varchar(255),
  gift_message_text         varchar(255),
  gift_wrap_level           varchar(255),
  invoice_data_invoice_requirement varchar(255),
  invoice_data_buyer_selected_invoice_category varchar(255),
  invoice_data_invoice_title varchar(255),
  invoice_data_invoice_information varchar(255),
  condition_note            varchar(255),
  condition_id              varchar(255),
  condition_subtype_id      varchar(255),
  scheduled_delivery_start_date varchar(255),
  scheduled_delivery_end_date varchar(255),
  mws_response              varchar(2000) not null,
  order_id                  bigint not null,
  is_handle                 tinyint(1) default 0,
  created_at                datetime,
  updated_at                datetime not null,
  constraint pk_amazon_order_item primary key (id))
;

create table amazon_order_schedule (
  id                        bigint auto_increment not null,
  config                    varchar(1000) not null,
  status                    integer,
  ip_address                varchar(255) not null,
  last_time                 datetime not null,
  created_at                datetime,
  updated_at                datetime not null,
  constraint ck_amazon_order_schedule_status check (status in (0,1,2)),
  constraint pk_amazon_order_schedule primary key (id))
;

create table amazon_report (
  id                        bigint auto_increment not null,
  report_request_id         varchar(255) not null,
  report_type               varchar(255),
  start_date                varchar(255),
  end_date                  varchar(255),
  submitted_date            varchar(255),
  report_processing_status  varchar(255),
  generated_report_id       varchar(255),
  started_processing_date   varchar(255),
  completed_date            varchar(255),
  url                       varchar(255) not null,
  app_name                  varchar(255) not null,
  status                    integer,
  path                      varchar(255),
  md5checksum               varchar(255),
  created_at                datetime,
  updated_at                datetime not null,
  constraint ck_amazon_report_status check (status in (0,1,2,3)),
  constraint uq_amazon_report_report_request_id unique (report_request_id),
  constraint pk_amazon_report primary key (id))
;

create table amazon_report_order_snapshot (
  id                        bigint auto_increment not null,
  amazon_order_id           varchar(255),
  merchant_order_id         varchar(255),
  purchase_date             varchar(255),
  last_updated_date         varchar(255),
  order_status              varchar(255),
  fulfillment_channel       varchar(255),
  sales_channel             varchar(255),
  order_channel             varchar(255),
  url                       varchar(255),
  ship_service_level        varchar(255),
  product_name              varchar(2000),
  sku                       varchar(255),
  asin                      varchar(255),
  item_status               varchar(255),
  quantity                  varchar(255),
  currency                  varchar(255),
  item_price                varchar(255),
  item_tax                  varchar(255),
  shipping_price            varchar(255),
  shipping_tax              varchar(255),
  gift_wrap_price           varchar(255),
  gift_wrap_tax             varchar(255),
  item_promotion_discount   varchar(255),
  ship_promotion_discount   varchar(255),
  ship_city                 varchar(255),
  ship_state                varchar(255),
  ship_postal_code          varchar(255),
  ship_country              varchar(255),
  promotion_ids             varchar(255),
  is_business_order         varchar(255),
  purchase_order_number     varchar(255),
  price_designation         varchar(255),
  report_id                 bigint not null,
  is_handle                 tinyint(1) default 0,
  created_at                datetime,
  updated_at                datetime not null,
  constraint pk_amazon_report_order_snapshot primary key (id))
;

create table amazon_report_schedule (
  id                        bigint auto_increment not null,
  config                    varchar(1000) not null,
  status                    integer,
  ip_address                varchar(255) not null,
  last_time                 datetime not null,
  created_at                datetime,
  updated_at                datetime not null,
  constraint ck_amazon_report_schedule_status check (status in (0,1,2)),
  constraint pk_amazon_report_schedule primary key (id))
;

create table cron_order (
  id                        bigint auto_increment not null,
  account_id                bigint,
  config                    TEXT,
  minute                    smallint,
  status                    integer,
  runnable                  tinyint(1) default 0,
  ap_address                varchar(255),
  account_name              varchar(255),
  paypal_id                 bigint,
  fire_at                   datetime,
  erp_account_id            bigint,
  created_at                datetime,
  updated_at                datetime not null,
  constraint ck_cron_order_status check (status in (0,1,2)),
  constraint pk_cron_order primary key (id))
;

create table ebay_log (
  id                        bigint auto_increment not null,
  content                   varchar(1000),
  time                      datetime,
  type                      integer,
  constraint ck_ebay_log_type check (type in (0,1,2)),
  constraint pk_ebay_log primary key (id))
;

create table ebay_monetary_detail (
  id                        bigint auto_increment not null,
  is_handled                tinyint(1) default 0,
  master_id                 bigint,
  type                      integer,
  status                    varchar(255),
  from_type                 varchar(255),
  from_name                 varchar(255),
  to_type                   varchar(255),
  to_name                   varchar(255),
  time                      datetime,
  amount                    decimal(15,3),
  amount_currency           varchar(255),
  reference_id_type         varchar(255),
  reference_id              varchar(255),
  fee                       decimal(15,3),
  fee_currency              varchar(255),
  refund_type               varchar(255),
  update_at                 datetime not null,
  constraint ck_ebay_monetary_detail_type check (type in (0,1,2)),
  constraint pk_ebay_monetary_detail primary key (id))
;

create table ebay_order_detail (
  id                        bigint auto_increment not null,
  master_id                 bigint,
  is_handled                tinyint(1) default 0,
  item_id                   varchar(255),
  sku                       varchar(255),
  quantity                  integer,
  price                     decimal(15,3),
  price_currency            varchar(255),
  transaction_id            varchar(255),
  shipping_carrier          varchar(255),
  shipping_tracking_num     varchar(255),
  sales_record_number       integer,
  total_tax_amount          decimal(15,3),
  total_tax_amount_currency varchar(255),
  create_at                 datetime,
  actual_shipping_cost      decimal(15,3),
  actual_shipping_cost_currency varchar(255),
  actual_handling_cost      decimal(15,3),
  actual_handling_cost_currency varchar(255),
  site                      varchar(255),
  tax_amount                decimal(15,3),
  tax_on_subtotal           decimal(15,3),
  tax_on_shipping           decimal(15,3),
  tax_on_handling           decimal(15,3),
  waste_recycling_fee_tax_amount decimal(15,3),
  title                     varchar(2000),
  estimated_delivery_time_min datetime,
  estimated_delivery_time_max datetime,
  buyer_email               varchar(255),
  static_alias              varchar(255),
  update_at                 datetime not null,
  constraint pk_ebay_order_detail primary key (id))
;

create table ebay_order_master (
  id                        bigint auto_increment not null,
  order_id                  varchar(255),
  buyer_id                  varchar(255),
  shipping_service          varchar(255),
  shipped_time              datetime,
  sales_record_number       integer,
  tax                       decimal(15,3),
  tax_currency              varchar(255),
  tax_percent               decimal(15,3),
  tax_state                 varchar(255),
  shipping_included_in_tax  tinyint(1) default 0,
  insurance                 decimal(15,3),
  insurance_currency        varchar(255),
  total                     decimal(15,3),
  total_currency            varchar(255),
  subtotal                  decimal(15,3),
  subtotal_currency         varchar(255),
  amount_adjust             decimal(15,3),
  amount_adjust_currency    varchar(255),
  amount_paid               decimal(15,3),
  amount_paid_currency      varchar(255),
  amount_saved              decimal(15,3),
  amount_saved_currency     varchar(255),
  payment_method            varchar(255),
  checkout_status           varchar(255),
  last_modified_time        datetime,
  create_date               datetime,
  paypal_date               datetime,
  paypal_email              varchar(255),
  paypal_status             varchar(255),
  paid_time                 datetime,
  order_status              varchar(255),
  seller_email              varchar(255),
  seller_user_id            varchar(255),
  eias_token                varchar(255),
  integrated_merchant_credit_card_enabled tinyint(1) default 0,
  fire_at_date              datetime,
  buyer_name                varchar(255),
  buyer_phone               varchar(255),
  buyer_street1             varchar(255),
  buyer_street2             varchar(255),
  buyer_city                varchar(255),
  buyer_state               varchar(255),
  buyer_zip                 varchar(255),
  buyer_country             varchar(255),
  is_multi_leg_shipping     tinyint(1) default 0,
  shipping_recipient_name   varchar(255),
  shipping_recipient_phone  varchar(255),
  shipping_street1          varchar(255),
  shipping_street2          varchar(255),
  shipping_city             varchar(255),
  shipping_state            varchar(255),
  shipping_zip              varchar(255),
  shipping_country          varchar(255),
  shipping_reference_id     varchar(255),
  get_it_fast               tinyint(1) default 0,
  shipping_cost             decimal(15,3),
  shipping_cost_currency    varchar(255),
  global_shipping_service   varchar(255),
  global_shipping_cost      decimal(15,3),
  global_shipping_cost_currency varchar(255),
  global_shipping_import_charge decimal(15,3),
  global_shipping_import_charge_currency varchar(255),
  erp_platform_account_id   bigint,
  order_detail_num          integer,
  monetary_detail_num       integer,
  paypal_failed             tinyint(1) default 0,
  creating_user_role        varchar(255),
  is_handled                tinyint(1) default 0,
  update_at                 datetime not null,
  constraint pk_ebay_order_master primary key (id))
;

create table ebay_paypal (
  id                        bigint auto_increment not null,
  transaction_id            varchar(255),
  retry_times               integer,
  paypal_config_id          bigint,
  seller_email              varchar(255),
  update_at                 datetime not null,
  constraint pk_ebay_paypal primary key (id))
;

create table ebay_send_data (
  id                        bigint auto_increment not null,
  runnable                  tinyint(1) default 0,
  ip                        varchar(255),
  update_at                 datetime not null,
  constraint pk_ebay_send_data primary key (id))
;

create table groups (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  created_at                datetime not null,
  updated_at                datetime,
  permissions               varchar(255),
  version                   datetime not null,
  constraint pk_groups primary key (id))
;

create table order_log (
  id                        bigint auto_increment not null,
  created_at                datetime,
  size                      bigint,
  status                    tinyint(1) default 0,
  time                      bigint,
  cron_order_id             bigint,
  constraint pk_order_log primary key (id))
;

create table order_user (
  id                        bigint auto_increment not null,
  account_id                bigint,
  config                    TEXT,
  minute                    smallint,
  status                    integer,
  ip                        varchar(255),
  fire_at                   datetime,
  created_at                datetime,
  updated_at                datetime not null,
  constraint ck_order_user_status check (status in (0,1)),
  constraint pk_order_user primary key (id))
;

create table paypal_config (
  id                        bigint auto_increment not null,
  account                   varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  signature                 varchar(255),
  appid                     varchar(255),
  subject                   varchar(255),
  constraint pk_paypal_config primary key (id))
;

create table platform_account (
  id                        bigint auto_increment not null,
  platform_id               bigint,
  name                      varchar(100) not null,
  abbreviation              varchar(10) not null,
  service_email             varchar(255),
  bill_email                varchar(255),
  created_at                datetime,
  version                   datetime not null,
  constraint uq_platform_account_name unique (name),
  constraint uq_platform_account_abbreviation unique (abbreviation),
  constraint pk_platform_account primary key (id))
;

create table platform_master (
  id                        bigint auto_increment not null,
  name                      varchar(100) not null,
  abbreviation              varchar(10) not null,
  created_at                datetime,
  channel                   varchar(255),
  version                   datetime not null,
  constraint uq_platform_master_name unique (name),
  constraint uq_platform_master_abbreviation unique (abbreviation),
  constraint pk_platform_master primary key (id))
;

create table queue_error (
  id                        bigint auto_increment not null,
  line                      integer,
  content                   varchar(255),
  description               TEXT,
  constraint pk_queue_error primary key (id))
;

create table queue_master (
  id                        bigint auto_increment not null,
  action                    varchar(255) not null,
  params                    varchar(255),
  agent_id                  bigint,
  file_name                 varchar(255),
  status                    integer,
  total                     integer,
  approved                  integer,
  pending                   integer,
  reject                    integer,
  error                     integer,
  created_at                datetime,
  update_at                 datetime,
  version                   datetime not null,
  constraint ck_queue_master_status check (status in (0,1,2,3)),
  constraint pk_queue_master primary key (id))
;

create table report_list (
  id                        bigint auto_increment not null,
  request_id                varchar(255) not null,
  type                      varchar(255),
  status                    tinyint(1) default 0,
  constraint uq_report_list_request_id unique (request_id),
  constraint pk_report_list primary key (id))
;

create table s3file (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  mime                      varchar(255),
  size                      bigint,
  path                      varchar(255),
  agent_id                  bigint,
  cron_order_id             bigint,
  created_at                datetime,
  updated_at                datetime,
  report_id                 varchar(255),
  type                      varchar(255),
  is_handle                 tinyint(1) default 0,
  action                    varchar(255),
  file_name                 varchar(255),
  constraint pk_s3file primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255) not null,
  auth_token                varchar(255),
  sha_password              varbinary(64) not null,
  full_name                 varchar(255) not null,
  created_at                datetime,
  version                   datetime not null,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;


create table queue_master_s3file (
  queue_master_id                bigint not null,
  s3file_id                      bigint not null,
  constraint pk_queue_master_s3file primary key (queue_master_id, s3file_id))
;

create table queue_master_queue_error (
  queue_master_id                bigint not null,
  queue_error_id                 bigint not null,
  constraint pk_queue_master_queue_error primary key (queue_master_id, queue_error_id))
;

create table groups_users (
  user_id                        bigint not null,
  groups_id                      bigint not null,
  constraint pk_groups_users primary key (user_id, groups_id))
;
alter table cron_order add constraint fk_cron_order_account_1 foreign key (account_id) references platform_account (id) on delete restrict on update restrict;
create index ix_cron_order_account_1 on cron_order (account_id);
alter table ebay_monetary_detail add constraint fk_ebay_monetary_detail_master_2 foreign key (master_id) references ebay_order_master (id) on delete restrict on update restrict;
create index ix_ebay_monetary_detail_master_2 on ebay_monetary_detail (master_id);
alter table ebay_order_detail add constraint fk_ebay_order_detail_master_3 foreign key (master_id) references ebay_order_master (id) on delete restrict on update restrict;
create index ix_ebay_order_detail_master_3 on ebay_order_detail (master_id);
alter table order_log add constraint fk_order_log_cronOrder_4 foreign key (cron_order_id) references cron_order (id) on delete restrict on update restrict;
create index ix_order_log_cronOrder_4 on order_log (cron_order_id);
alter table order_user add constraint fk_order_user_account_5 foreign key (account_id) references platform_account (id) on delete restrict on update restrict;
create index ix_order_user_account_5 on order_user (account_id);
alter table platform_account add constraint fk_platform_account_platform_6 foreign key (platform_id) references platform_master (id) on delete restrict on update restrict;
create index ix_platform_account_platform_6 on platform_account (platform_id);
alter table queue_master add constraint fk_queue_master_agent_7 foreign key (agent_id) references user (id) on delete restrict on update restrict;
create index ix_queue_master_agent_7 on queue_master (agent_id);
alter table s3file add constraint fk_s3file_agent_8 foreign key (agent_id) references user (id) on delete restrict on update restrict;
create index ix_s3file_agent_8 on s3file (agent_id);
alter table s3file add constraint fk_s3file_cronOrder_9 foreign key (cron_order_id) references cron_order (id) on delete restrict on update restrict;
create index ix_s3file_cronOrder_9 on s3file (cron_order_id);



alter table queue_master_s3file add constraint fk_queue_master_s3file_queue_master_01 foreign key (queue_master_id) references queue_master (id) on delete restrict on update restrict;

alter table queue_master_s3file add constraint fk_queue_master_s3file_s3file_02 foreign key (s3file_id) references s3file (id) on delete restrict on update restrict;

alter table queue_master_queue_error add constraint fk_queue_master_queue_error_queue_master_01 foreign key (queue_master_id) references queue_master (id) on delete restrict on update restrict;

alter table queue_master_queue_error add constraint fk_queue_master_queue_error_queue_error_02 foreign key (queue_error_id) references queue_error (id) on delete restrict on update restrict;

alter table groups_users add constraint fk_groups_users_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table groups_users add constraint fk_groups_users_groups_02 foreign key (groups_id) references groups (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table amazon_order;

drop table amazon_order_item;

drop table amazon_order_schedule;

drop table amazon_report;

drop table amazon_report_order_snapshot;

drop table amazon_report_schedule;

drop table cron_order;

drop table ebay_log;

drop table ebay_monetary_detail;

drop table ebay_order_detail;

drop table ebay_order_master;

drop table ebay_paypal;

drop table ebay_send_data;

drop table groups;

drop table groups_users;

drop table order_log;

drop table order_user;

drop table paypal_config;

drop table platform_account;

drop table platform_master;

drop table queue_error;

drop table queue_master;

drop table queue_master_s3file;

drop table queue_master_queue_error;

drop table report_list;

drop table s3file;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

