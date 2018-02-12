package com.qount.invoice.model;

import java.util.List;

public class FilterModel {

		public FilterModel() {

		}

		public FilterModel(String filterName, String operator, List<String> values) {
			this.filterName = filterName;
			this.operator = operator;
			this.values = values;
		}

		private String filterName;
		private String operator;
		private String filterDisplayName;
		private String type;
		List<String> values;

		public String getFilterName() {
			return filterName;
		}

		public void setFilterName(String filterName) {
			this.filterName = filterName;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getFilterDisplayName() {
			return filterDisplayName;
		}

		public void setFilterDisplayName(String filterDisplayName) {
			this.filterDisplayName = filterDisplayName;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<String> getValues() {
			return values;
		}

		public void setValues(List<String> values) {
			this.values = values;
		}

}
