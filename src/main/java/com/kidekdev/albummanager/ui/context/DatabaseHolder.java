package com.kidekdev.albummanager.ui.context;


import com.kidekdev.albummanager.database.dao.DynamicResourceDatabaseFacade;
import com.kidekdev.albummanager.database.dao.ImportRuleDatabaseFacade;
import com.kidekdev.albummanager.database.dao.ResourceDatabaseFacade;
import com.kidekdev.albummanager.database.dao.TagDatabaseFacade;
import com.kidekdev.albummanager.database.dao.impl.DynamicResourceDatabaseFacadeImpl;
import com.kidekdev.albummanager.database.dao.impl.ImportRuleDatabaseFacadeImpl;
import com.kidekdev.albummanager.database.dao.impl.ResourceDatabaseFacadeImpl;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseHolder {

  public static ResourceDatabaseFacade resource = new ResourceDatabaseFacadeImpl();
  public static TagDatabaseFacade tag;
  public static ImportRuleDatabaseFacade importRule = new ImportRuleDatabaseFacadeImpl();
  public static DynamicResourceDatabaseFacade dynamicResource = new DynamicResourceDatabaseFacadeImpl();
}
