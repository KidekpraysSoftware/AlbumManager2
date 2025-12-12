package com.kidekdev.albummanager.ui.context;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DatabaseCache {

public static final Map<String, Path> resourceCache = new HashMap<>(); //key - sha256 файла, Path - путь до файла.

}
