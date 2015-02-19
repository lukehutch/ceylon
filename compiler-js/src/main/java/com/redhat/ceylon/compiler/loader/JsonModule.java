package com.redhat.ceylon.compiler.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.redhat.ceylon.compiler.js.CompilerErrorException;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;

public class JsonModule extends Module {

    private Map<String,Object> model;
    private boolean loaded = false;

    public void setModel(Map<String, Object> value) {
        if (model != null) {
            final String modName = (String)model.get("$mod-name");
            final String modVers = (String)model.get("$mod-version");
            if (!(Objects.equals(modName, value.get("$mod-name"))
                    && Objects.equals(modVers, value.get("$mod-version")))) {
                throw new IllegalStateException("JsonModule " + modName+"/"+modVers
                        + " receives new module " + value.get("$mod-name")+"/"+value.get("$mod-version"));
            }
            return;
        }
        model = value;
        final String binVersion = (String)model.get("$mod-bin");
        final int dotidx = binVersion.indexOf('.');
        setMajor(Integer.parseInt((String)binVersion.substring(0,dotidx), 10));
        setMinor(Integer.parseInt((String)binVersion.substring(dotidx+1), 10));
    }
    public Map<String, Object> getModel() {
        return model;
    }
    @SuppressWarnings("unchecked")
    public List<String> getDocs() {
        return (List<String>)model.get("$mod-DOC$");
    }

    void loadDeclarations() {
        if (loaded)return;
        if (model != null) {
            if (!loaded) {
                loaded=true;
                ArrayList<JsonPackage> pks = new ArrayList<>(model.size());
                for (Map.Entry<String, Object> e : model.entrySet()) {
                    if (!e.getKey().startsWith("$mod-")) {
                        JsonPackage p = new JsonPackage(e.getKey());
                        p.setModule(this);
                        pks.add(p);
                        getPackages().add(p);
                    }
                }
                for (JsonPackage p : pks) {
                    p.loadDeclarations();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String,Object> getModelForPackage(String name) {
        return model == null ? null : (Map<String,Object>)model.get(name);
    }

    @Override
    public Package getPackage(String name) {
        if ("default".equals(name)) {
            name = "";
        }
        Package p = getDirectPackage(name);
        if (p != null) {
            return p;
        }
        for (ModuleImport imp : getImports()) {
            final Module mod = imp.getModule();
            p = mod.getDirectPackage(name);
            if (p != null) {
                return p;
            }
            for (ModuleImport im2 : mod.getImports()) {
                if (im2.isExport()) {
                    p = im2.getModule().getDirectPackage(name);
                }
                if (p != null) {
                    return p;
                }
            }
        }
        throw new CompilerErrorException("Package " + name + " not available in module " + this);
    }

    public List<ModuleImport> getImports() {
        final List<ModuleImport> s = super.getImports();
        ArrayList<ModuleImport> r = new ArrayList<>(s.size());
        r.addAll(s);
        return r;
    }

}