package org.exoplatform.social.core.space.spi;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.space.SpaceApplication;
import org.exoplatform.social.core.space.SpaceTemplate;
import org.exoplatform.social.core.space.SpaceTemplateConfigPlugin;
import org.exoplatform.social.core.space.impl.DefaultSpaceApplicationHandler;
import org.exoplatform.social.core.space.impl.SpaceTemplateServiceImpl;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.IdentityStorageException;
import org.exoplatform.social.core.storage.api.IdentityStorage;
import org.exoplatform.social.core.storage.impl.StorageUtils;
import org.exoplatform.social.core.test.AbstractCoreTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SpaceTemplateServiceTest extends AbstractCoreTest {
  private SpaceTemplateService spaceTemplateService;
  private IdentityStorage identityStorage;
  private List<Space> tearDownSpaceList;

  @Override
  public void setUp() {
    spaceTemplateService = CommonsUtils.getService(SpaceTemplateService.class);
    identityStorage = CommonsUtils.getService(IdentityStorage.class);
    spaceService = CommonsUtils.getService(SpaceService.class);
    tearDownSpaceList = new ArrayList<>();
  }

  @Override
  public void tearDown() throws Exception {
    end();
    begin();

    for (Space space : tearDownSpaceList) {
      Identity spaceIdentity = identityStorage.findIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());
      if (spaceIdentity != null) {
        try {
          identityStorage.deleteIdentity(spaceIdentity);
        } catch (IdentityStorageException e) {
          // It's expected on some identities that could be deleted in tests
        }
      }
      try {
        spaceService.deleteSpace(space);
      } catch (Exception e) {
        // It's expected on some entities that could be deleted in tests
      }
    }

    StorageUtils.persist();
    super.tearDown();
  }

  /**
   * Test {@link SpaceTemplateService#getSpaceTemplates()}
   *
   */
  public void testGetSpaceTemplates() {
    // When
    List<SpaceTemplate> templates = spaceTemplateService.getSpaceTemplates();
    // Then
    assertTrue(Collections.unmodifiableList(Collections.EMPTY_LIST).getClass().isInstance(templates));
  }

  /**
   * Test {@link SpaceTemplateService#getSpaceTemplateByName(String)}
   *
   */
  public void testGetSpaceTemplateByName() {
    // When
    SpaceTemplate template = spaceTemplateService.getSpaceTemplateByName("classic");
    SpaceTemplate templateFake = spaceTemplateService.getSpaceTemplateByName("fake");
    // Then
    assertEquals(template.getName(), "classic");
    assertNull(templateFake);
  }

  /**
   * Test {@link SpaceTemplateService#registerSpaceTemplatePlugin(SpaceTemplateConfigPlugin)}
   *
   */
  public void testRegisterSpaceTemplatePlugin() {
    SpaceApplication homeApplication = new SpaceApplication();
    homeApplication.setAppTitle("fakeHome");
    homeApplication.setPortletApp("fakeHomeApp");
    homeApplication.setPortletName("fakeHomeName");

    List<SpaceApplication> applicationList = new ArrayList<>();
    for (int i=0; i<3; i++) {
      SpaceApplication app = new SpaceApplication();
      app.setAppTitle("fakeTitle" + i);
      app.setPortletApp("fakeApp" + i);
      app.setPortletName("fakeName" + i);
      applicationList.add(app);
    }
    SpaceTemplate spaceTemplate = new SpaceTemplate();
    spaceTemplate.setName("custom");
    spaceTemplate.setVisibility("private");
    spaceTemplate.setRegistration("open");
    spaceTemplate.setHomeApplication(homeApplication);
    spaceTemplate.setSpaceApplicationList(applicationList);
    InitParams params = new InitParams();
    ObjectParameter objParam = new ObjectParameter();
    objParam.setName("template");
    objParam.setObject(spaceTemplate);
    params.addParameter(objParam);
    //Given
    assertEquals(1, spaceTemplateService.getSpaceTemplates().size());
    //when
    spaceTemplateService.registerSpaceTemplatePlugin(new SpaceTemplateConfigPlugin(params));
    // Then
    assertEquals(2, spaceTemplateService.getSpaceTemplates().size());
  }

  /**
   * Test {@link SpaceTemplateService#extendSpaceTemplatePlugin(SpaceTemplateConfigPlugin)}
   *
   */
  public void testExtendSpaceTemplatePlugin() {
    List<SpaceApplication> applicationList = new ArrayList<>();
    for (int i=0; i<3; i++) {
      SpaceApplication app = new SpaceApplication();
      app.setAppTitle("fakeTitle" + i);
      app.setPortletApp("fakeApp" + i);
      app.setPortletName("fakeName" + i);
      app.setOrder(4 + i);
      applicationList.add(app);
    }
    SpaceTemplate spaceTemplate = new SpaceTemplate();
    spaceTemplate.setName("classic");
    spaceTemplate.setSpaceApplicationList(applicationList);
    InitParams params = new InitParams();
    ObjectParameter objParam = new ObjectParameter();
    objParam.setName("template");
    objParam.setObject(spaceTemplate);
    params.addParameter(objParam);
    //Given
    SpaceTemplate template = spaceTemplateService.getSpaceTemplateByName("classic");
    assertEquals(3, template.getSpaceApplicationList().size());
    //when
    spaceTemplateService.extendSpaceTemplatePlugin(new SpaceTemplateConfigPlugin(params));
    // Then
    ((SpaceTemplateServiceImpl) spaceTemplateService).start();
    template = spaceTemplateService.getSpaceTemplateByName("classic");
    assertEquals(6, template.getSpaceApplicationList().size());
  }

  /**
   * Test {@link SpaceTemplateService#registerSpaceApplicationHandler(SpaceApplicationHandler)}
   *
   */
  public void testRegisterSpaceApplicationHandler() {
    //Given
    Map<String, SpaceApplicationHandler> handlerMap = spaceTemplateService.getSpaceApplicationHandlers();
    assertEquals(1, handlerMap.size());
    InitParams params = new InitParams();
    ValueParam valueParam = new ValueParam();
    valueParam.setName("templateName");
    valueParam.setValue("custom");
    params.addParameter(valueParam);
    SpaceApplicationHandler applicationHandler = new DefaultSpaceApplicationHandler(params, null, null, null);
    //when
    spaceTemplateService.registerSpaceApplicationHandler(applicationHandler);
    //then
    handlerMap = spaceTemplateService.getSpaceApplicationHandlers();
    assertEquals(2, handlerMap.size());
  }

  /**
   * Test {@link SpaceTemplateService#getSpaceApplicationHandlers()}
   *
   */
  public void testGetSpaceApplicationHandlers() {
    // When
    Map<String, SpaceApplicationHandler> handlers = spaceTemplateService.getSpaceApplicationHandlers();
    // Then
    assertTrue(Collections.unmodifiableMap(Collections.EMPTY_MAP).getClass().isInstance(handlers));
  }

  /**
   * Test {@link SpaceTemplateService#getDefaultSpaceTemplate()}
   *
   */
  public void testGetDefaultSpaceTemplate() {
    assertEquals("classic", spaceTemplateService.getDefaultSpaceTemplate());
  }

  /**
   * Test {@link SpaceTemplateService#initSpaceApplications(Space, SpaceApplicationHandler)}
   *
   */
  public void testInitSpaceApplications() throws Exception {
    // TODO
  }

  /**
   * Test {@link SpaceTemplateService#setApp(Space, String, String, boolean, String)}
   *
   */
  public void testSetApp() throws Exception {
    startSessionAs("root");
    Space space = createSpace("mySpace", "root");
    assertNull(space.getApp());
    //when
    spaceTemplateService.setApp(space, "appId", "appName", true, Space.ACTIVE_STATUS);
    //then
    assertEquals("appId:appName:true:active", space.getApp());
  }

  private Space createSpace(String spaceName, String creator) throws Exception {
    try {
      Space space = new Space();
      space.setDisplayName(spaceName);
      space.setPrettyName(spaceName);
      space.setGroupId("/spaces/" + space.getPrettyName());
      space.setRegistration(Space.OPEN);
      space.setDescription("description of space" + spaceName);
      space.setTemplate(DefaultSpaceApplicationHandler.NAME);
      space.setVisibility(Space.PRIVATE);
      space.setRegistration(Space.OPEN);
      space.setPriority(Space.INTERMEDIATE_PRIORITY);
      String[] managers = new String[] {creator};
      String[] members = new String[] {creator};
      space.setManagers(managers);
      space.setMembers(members);
      spaceService.saveSpace(space, true);
      tearDownSpaceList.add(space);
      return space;
    } finally {
      StorageUtils.persist();
    }
  }
}
