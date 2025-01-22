	/**
	 * Ajout d'un enregistrement
	 * @param data L'enregistrement à ajouter
	 * @throws DataSetException en cas d'erreur...
	 */
	public void add(T data) throws DataSetException {
		if (data == null) {
			throw new DataSetException("Pas de donnée !!!");
		}

		if (!isActive()) {
			open();
		}

		try {
			append();
			setRowData(data);
			post();
		} catch (DataSetException e) {
			cancel();
			throw e;
		}
	}

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
