package com.qount.invoice.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class InvoiceFilter {

		public InvoiceFilter() {

		}

		private String asOfDate; 
		@JsonIgnore
		private List<FilterModel> filters;

		public String getAsOfDate() {
			return asOfDate;
		}

		public void setAsOfDate(String asOfDate) {
			this.asOfDate = asOfDate;
		}

		public List<FilterModel> getFilters() {
			return filters;
		}

		public void setFilters(List<FilterModel> filters) {
			this.filters = filters;
		}
 
		
}
