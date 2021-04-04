create table balance (
                         id bigserial,
                         order_ids varchar(255),
                         order_last_update_date_time_utc timestamp,
                         order_line_balance_change decimal,                                 -- order_line_custom_field_purchase_detail_balance_change decimal,
                         order_line_quantity decimal,
                         order_line_price_of_line decimal,
                         customer_discount_card_number varchar(255),
                         primary key (id)
);