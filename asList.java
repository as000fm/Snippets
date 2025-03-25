	/**
	 * Extrait les données de la vue sous forme de liste
	 * @return les données de la vue sous forme de liste
	 * @throws DataSetException en cas d'erreur...
	 */
	public List<T> asList() throws DataSetException {
		if (!isActive()) {
			open();
		}

		List<T> list = new ArrayList<T>();

		while (!isEof()) {
			list.add(getRowData());

			next();
		}

		return list;
	}

	/**
	 * Extrait les données de la vue sous forme de liste
	 * @param max Le nombre maximum d'enregistrements à extraire
	 * @return les données de la vue sous forme de liste
	 * @throws DataSetException en cas d'erreur...
	 */
	public List<T> asList(int max) throws DataSetException {
		if (!isActive()) {
			open();
		}

		List<T> list = new ArrayList<T>();

		while ((max > 0) && !isEof()) {
			max--;

			list.add(getRowData());

			next();
		}

		return list;
	}
