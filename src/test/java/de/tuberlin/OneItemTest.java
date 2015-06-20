package de.tuberlin;

import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.common.OneItemType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Created by Andi on 10.05.2015.
 */
public class OneItemTest {

    @Test
    public void testNullJsonParseItems() {
        try {
            OneItem.parseItemsFromJson(null);
            Assert.fail();
        } catch (org.json.simple.parser.ParseException e) {
        } catch (OneDriveException e) {
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testNullJsonParse() {

        try {
            OneItem.fromJSON(null);
            Assert.fail();
        } catch (org.json.simple.parser.ParseException e) {
        } catch (OneDriveException e) {
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testParseEmptyOneItem() {
        String json = "{}";
        OneItem folder = null;
        try {
            folder = OneItem.fromJSON(json);
        } catch (org.json.simple.parser.ParseException e) {
            Assert.fail();
        } catch (OneDriveException e) {
            Assert.fail();
        }

        Assert.assertEquals("", folder.getId());
        Assert.assertEquals("", folder.getName());
        Assert.assertEquals(0, folder.getCreatedBy().size());
        Assert.assertEquals(0, folder.getCreatedDateTime());
        Assert.assertEquals(0, folder.getLastModifiedBy().size());
        Assert.assertEquals(0, folder.getLastModifiedDateTime());
        Assert.assertEquals("", folder.getCTag());
        Assert.assertEquals("", folder.getETag());
        Assert.assertEquals(0, folder.getSize());
        Assert.assertEquals("", folder.getWebUrl());
    }

    @Test
    public void testParseEmptyOneItems() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(2, items.size());
    }

    @Test
    public void testParseEmptyOneFiles() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.FILE);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(1, items.size());
    }

    @Test
    public void testParseEmptyOneFolder() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.FOLDER);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(1, items.size());
    }

    @Test
    public void testParseCorruptItems() {
        String json = "{\"val\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.ALL);
            Assert.fail();
        } catch (OneDriveException | org.json.simple.parser.ParseException e) {

        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testParseDate() {
        String json = "{\"createdDateTime\":\"2015-05-01T10:30:19.55Z\"}";
        OneItem item = null;
        try {
            item = OneItem.fromJSON(json);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(1430469019L, item.getCreatedDateTime());

        json = "{\"createdDateTime\":\"2015-05-01T\"}";
        try {
            item = OneItem.fromJSON(json);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(0L, item.getCreatedDateTime());
    }

    @Test
    public void setNullApi() {
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");
        } catch (Exception e) {
            Assert.fail();
        }
        try {
            item.setApi(null);
            Assert.fail();
        } catch (OneDriveException e) {
        }
    }

    @Test
    public void testSetApi() {
        ConcreteOneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");
        } catch (Exception e){
            Assert.fail();
        }
        try {
            item.setApi(api);
        } catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testDeleteItem() {
        ConcreteOneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");

            Mockito.doReturn(true).when(api).deleteItem(item);
            item.setApi(api);
            item.delete();
        } catch (Exception e){
            Assert.fail();
        }
    }
}
