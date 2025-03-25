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

