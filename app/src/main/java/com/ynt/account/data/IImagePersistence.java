package com.ynt.account.data;

public interface IImagePersistence {
	/**
	 * 装载数据
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public ImageModel[] load(String filter) throws Exception;
	/**
	 * 保存数据
	 * @param model
	 * @throws Exception
	 */
	public void Save(ImageModel[] models) throws Exception;
}
