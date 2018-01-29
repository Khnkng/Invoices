package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.DiscountsRanges;

public interface DiscountsRangesDAO {

	DiscountsRanges get(Connection conn, DiscountsRanges discounts_ranges);

	List<DiscountsRanges> getAll(Connection conn, DiscountsRanges input);

	String delete(Connection conn, String discountId);

	boolean deleteByIds(Connection conn, String commaSeparatedIds);

	List<DiscountsRanges> create(Connection conn, List<DiscountsRanges> discounts_ranges,String discount_id);


}
