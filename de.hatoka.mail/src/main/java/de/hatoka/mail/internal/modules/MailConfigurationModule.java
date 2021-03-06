package de.hatoka.mail.internal.modules;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.hatoka.mail.capi.config.SmtpConfiguration;
import de.hatoka.mail.internal.service.SmtpConfigurationSystemEnvImpl;

public class MailConfigurationModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(SmtpConfiguration.class).to(SmtpConfigurationSystemEnvImpl.class).asEagerSingleton();
    }

}
