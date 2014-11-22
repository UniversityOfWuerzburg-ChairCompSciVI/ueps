package de.uniwue.info6.webapp.lists;

import java.io.Serializable;

public class ColumnModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String header;
		private String index;
		private String indexVariable;

		public ColumnModel(String header, String index) {
			this.header = header;
			this.index = index;
			this.indexVariable = "value_" + index;
		}

		public String getHeader() {
			return header;
		}

		public String getIndex() {
			return index;
		}

		/**
		 * @return the indexVariable
		 */
		public String getIndexVariable() {
			return indexVariable;
		}

		/**
		 * @param indexVariable
		 *            the indexVariable to set
		 */
		public void setIndexVariable(String indexVariable) {
			this.indexVariable = indexVariable;
		}

}
