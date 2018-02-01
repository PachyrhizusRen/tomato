package com.forufamily.gradle.plugin.util

class FileLockFinder {
    private final File mRoot
    private final String logPrefix

    FileLockFinder(String path, String logPrefix = "") {
        this(new File(path), logPrefix)
    }

    FileLockFinder(File dir, String logPrefix = "") {
        this.mRoot = dir
        this.logPrefix = logPrefix
    }

    void start() {
        if (null != mRoot && mRoot.exists() && mRoot.isDirectory()) {
            findLockedFiles(findFiles(mRoot), logPrefix)
        }
    }

    private static Collection<File> findFiles(File dir) {
        def result = new ArrayList<>()
        def files = dir.listFiles()
        if (null != files) {
            files.each {
                if (it.isDirectory()) {
                    result.addAll(findFiles(it))
                } else {
                    result.add(it)
                }
            }
        }

        return result
    }

    static boolean hasLockedFiles(Collection<File> files) {
        return files.find { !canRename(it) }
    }

    static boolean isFileLocked(File file) {
        if (file.isDirectory()) {
            return hasLockedFiles(findFiles(file))
        }
        return canRename(file)
    }

    static void findLockedFiles(Collection<File> files, String logPrefix = "") {
        files.each { findLockedFile(it, logPrefix) }
    }

    static void findLockedFile(File file, String logPrefix = "") {
        if (file.isDirectory()) findLockedFiles(findFiles(file))
        else if (!canRename(file)) {
            "${logPrefix}File[${file.absolutePath}] is locked!".info()
        }
    }

    private static boolean canRename(File file) {
        def rename = new File(file.absolutePath + "_")
        def result = file.renameTo(rename)
        if (result) rename.renameTo(file)
        return result
    }
}
