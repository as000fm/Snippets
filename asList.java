	/**
	 * Ajoute un filtre à la vue et l'active
	 * @param filter La partie SQL du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveFilter(String filter) throws DataSetException {
		setFilter(filter);
		setFilterActive(true);
	}

	/**
	 * Ajoute un filtre à la vue et l'active
	 * @param filter La partie SQL du filtre
	 * @param params Les paramètres du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveFilter(String filter, DataFieldList params) throws DataSetException {
		setFilter(filter, params);
		setFilterActive(true);
	}

	/**
	 * Ajoute un filtre à la vue et l'active basé sur le contenu des paramètres, sensible à la case et utilisant la conjonction SQL "AND"
	 * @param params Les paramètres du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveFilter(DataFieldList params) throws DataSetException {
		setFilter(params);
		setFilterActive(true);
	}

	/**
	 * Ajoute un filtre à la vue et l'active basé sur le contenu des paramètres et utilisant la conjonction SQL "AND"
	 * @param params Les paramètres du filtre
	 * @param ignoreCase Indicateur d'ignorer la case
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveFilter(DataFieldList params, boolean ignoreCase) throws DataSetException {
		setFilter(params, ignoreCase);
		setFilterActive(true);
	}

	/**
	 * Ajoute un filtre à la vue et l'active basé sur le contenu des paramètres et sensible à la case
	 * @param params Les paramètres du filtre
	 * @param conjunction La conjonction à utiliser entre chaque paramètre (ex: AND)
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveFilter(DataFieldList params, String conjunction) throws DataSetException {
		setFilter(params, conjunction);
		setFilterActive(true);
	}

	/**
	 * Ajoute un filtre à la vue et l'active basé sur le contenu des paramètres
	 * @param params Les paramètres du filtre
	 * @param ignoreCase Indicateur d'ignorer la case
	 * @param conjunction La conjonction à utiliser entre chaque paramètre (ex: AND)
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveFilter(DataFieldList params, boolean ignoreCase, String conjunction) throws DataSetException {
		setFilter(params, ignoreCase, conjunction);
		setFilterActive(true);
	}


	/**
	 * Ajoute un filtre d'affichage à la vue et l'active
	 * @param filter La partie SQL du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveDisplayFilter(String filter) throws DataSetException {
		setDisplayFilter(filter);
		setDisplayFilterActive(true);
	}

	/**
	 * Ajoute un filtre d'affichage à la vue et l'active
	 * @param filter La partie SQL du filtre
	 * @param params Les paramètres du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveDisplayFilter(String filter, DataFieldList params) throws DataSetException {
		setDisplayFilter(filter, params);
		setDisplayFilterActive(true);
	}

	/**
	 * Ajoute un filtre d'affichage à la vue et l'active basé sur le contenu des paramètres et utilisant la conjonction SQL "AND"
	 * @param params Les paramètres du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveDisplayFilter(DataFieldList params) throws DataSetException {
		setDisplayFilter(params);
		setDisplayFilterActive(true);
	}

	/**
	 * Ajoute un filtre d'affichage à la vue et l'active basé sur le contenu des paramètres et utilisant la conjonction SQL "AND"
	 * @param params Les paramètres du filtre
	 * @param ignoreCase Indicateur d'ignorer la case
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveDisplayFilter(DataFieldList params, boolean ignoreCase) throws DataSetException {
		setDisplayFilter(params, ignoreCase);
		setDisplayFilterActive(true);
	}

	/**
	 * Ajoute un filtre d'affichage à la vue et l'active basé sur le contenu des paramètres et sensible à la case
	 * @param params Les paramètres du filtre
	 * @param conjunction La conjonction à utiliser entre chaque paramètre (ex: AND)
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveDisplayFilter(DataFieldList params, String conjunction) throws DataSetException {
		setDisplayFilter(params, conjunction);
		setDisplayFilterActive(true);
	}

	/**
	 * Ajoute un filtre d'affichage à la vue et l'active basé sur le contenu des paramètres
	 * @param params Les paramètres du filtre
	 * @param ignoreCase Indicateur d'ignorer la case
	 * @param conjunction La conjonction à utiliser entre chaque paramètre (ex: AND)
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveDisplayFilter(DataFieldList params, boolean ignoreCase, String conjunction) throws DataSetException {
		setDisplayFilter(params, ignoreCase, conjunction);
		setDisplayFilterActive(true);
	}

	/**
	 * Ajoute un filtre de recherche à la vue et l'active
	 * @param filter La partie SQL du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveSearchFilter(String filter) throws DataSetException {
		setSearchFilter(filter);
		setSearchFilterActive(true);
	}

	/**
	 * Ajoute un filtre de recherche à la vue et l'active
	 * @param filter La partie SQL du filtre
	 * @param params Les paramètres du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveSearchFilter(String filter, DataFieldList params) throws DataSetException {
		setSearchFilter(filter, params);
		setSearchFilterActive(true);
	}

	/**
	 * Ajoute un filtre de recherche à la vue et l'active basé sur le contenu des paramètres et utilisant la conjonction SQL "AND"
	 * @param params Les paramètres du filtre
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveSearchFilter(DataFieldList params) throws DataSetException {
		setSearchFilter(params);
		setSearchFilterActive(true);
	}

	/**
	 * Ajoute un filtre de recherche à la vue et l'active basé sur le contenu des paramètres et utilisant la conjonction SQL "AND"
	 * @param params Les paramètres du filtre
	 * @param ignoreCase Indicateur d'ignorer la case
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveSearchFilter(DataFieldList params, boolean ignoreCase) throws DataSetException {
		setSearchFilter(params, ignoreCase);
		setSearchFilterActive(true);
	}

	/**
	 * Ajoute un filtre de recherche à la vue et l'active basé sur le contenu des paramètres et sensible à la case
	 * @param params Les paramètres du filtre
	 * @param conjunction La conjonction à utiliser entre chaque paramètre (ex: AND)
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveSearchFilter(DataFieldList params, String conjunction) throws DataSetException {
		setSearchFilter(params, conjunction);
		setSearchFilterActive(true);
	}

	/**
	 * Ajoute un filtre de recherche à la vue et l'active basé sur le contenu des paramètres
	 * @param params Les paramètres du filtre
	 * @param ignoreCase Indicateur d'ignorer la case
	 * @param conjunction La conjonction à utiliser entre chaque paramètre (ex: AND)
	 * @throws DataSetException en cas d'erreur (voir aussi getLastError()...)
	 */
	public void setActiveSearchFilter(DataFieldList params, boolean ignoreCase, String conjunction) throws DataSetException {
		setSearchFilter(params, ignoreCase, conjunction);
		setSearchFilterActive(true);
	}
