package com.example.cooperadora_escuela;

public class PermissionManager {
    // Constructor privado para evitar instanciación
    private PermissionManager() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }

    public static boolean hasStoragePermission() {
        // Simplemente retornamos true ya que no necesitamos verificar permisos reales
        return true;
    }

    public static void checkAndRequestPermissions() {
        // Método vacío ya que no necesitamos solicitar permisos
        // Se eliminó el parámetro no utilizado
    }
}