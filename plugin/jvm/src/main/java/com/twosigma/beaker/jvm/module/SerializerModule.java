/*
 *  Copyright 2014 TWO SIGMA INVESTMENTS, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beaker.jvm.module;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.twosigma.beaker.shared.json.serializer.StringObject;
import com.twosigma.beaker.shared.json.serializer.StringObjectSerializer;
import com.twosigma.beaker.jvm.object.SimpleEvaluationObject;
import com.twosigma.beaker.jvm.object.SimpleEvaluationObjectSerializer;
import com.twosigma.beaker.jvm.object.EvaluationResultSerializer;
import com.twosigma.beaker.jvm.object.EvaluationResult;
import com.twosigma.beaker.jvm.object.TableDisplaySerializer;
import com.twosigma.beaker.jvm.object.TableDisplay;
import com.twosigma.beaker.jvm.object.ImageIconSerializer;
import com.twosigma.beaker.jvm.updater.ObservableUpdater;
import com.twosigma.beaker.jvm.updater.UpdateManager;
import javax.swing.ImageIcon;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.cometd.bayeux.server.BayeuxServer;

/**
 * The Guice module as the registry of mapping from classes to serializers
 */
public class SerializerModule
        extends AbstractModule {

  @Override
  protected void configure() {
    bind(SimpleEvaluationObjectSerializer.class);
    bind(EvaluationResultSerializer.class);
    bind(TableDisplaySerializer.class);
  }

  @Provides
  @Singleton
  public UpdateManager getUpdateManager(Injector injector) {
    BayeuxServer bayeuxServer = injector.getInstance(BayeuxServer.class);
    UpdateManager updateManager = new UpdateManager(bayeuxServer);
    updateManager.addUpdater(new ObservableUpdater());
    return updateManager;
  }

  @Provides
  @Singleton
  public ObjectMapper getObjectMapper(Injector injector) {
    ObjectMapper mapper = new ObjectMapper();

    SimpleModule module =
            new SimpleModule("MySerializer", new Version(1, 0, 0, null));

    module.addSerializer(StringObject.class, new StringObjectSerializer());
    SimpleEvaluationObjectSerializer seos = injector.getInstance(SimpleEvaluationObjectSerializer.class);
    module.addSerializer(SimpleEvaluationObject.class, seos);
    module.addSerializer(EvaluationResult.class, new EvaluationResultSerializer());
    module.addSerializer(TableDisplay.class, new TableDisplaySerializer());
    module.addSerializer(ImageIcon.class, new ImageIconSerializer());

    mapper.registerModule(module);

    SerializationConfig config = mapper.getSerializationConfig();

    // Pretty
    mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);

    // Manually serialize everything, either through mixin or serializer
    mapper.disable(SerializationConfig.Feature.AUTO_DETECT_GETTERS);
    mapper.disable(SerializationConfig.Feature.AUTO_DETECT_IS_GETTERS);
    mapper.disable(SerializationConfig.Feature.AUTO_DETECT_FIELDS);

    return mapper;
  }

  @Provides
  @Singleton
  public JacksonJsonProvider getJackson(ObjectMapper mapper) {
    return new JacksonJsonProvider(mapper);
  }
}
