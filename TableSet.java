
	@Override
	public void close() throws DataSetException {
		if (isActive()) {
			if (getMode().isAppendBatchMode()) {
				applyBatch();
			}
		}

		super.close();
	}

	public void replace() throws DataSetException {
		if (!getMode().isAppendBatchMode()) {
			if (isEmpty()) {
				append();
			} else {
				edit();
			}
		}
	}

	public void appendBatch() throws DataSetException {
		if (!isActive()) {
			open();
		}

		if (!getMode().isAppendBatchMode()) {
			forceBrowseMode();
			setMode(ModesTypes.APPEND_BATCH_MODE);
			this.appendBatchModeValues.clear();
		}

		try {
			setEditRecord(new Record(0, getMetaData().duplicate().clearValues()));
			assignDefaultValues(getEditRecord().getFieldList());
		} catch (DataFieldException e) {
			throw new DataSetException(e.getMessage());
		}
	}
