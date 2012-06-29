package com.cryptic.imed.app;

import com.google.inject.AbstractModule;

/**
 * @author sharafat
 */
public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(AbstractDbHelper.class);
    }
}
