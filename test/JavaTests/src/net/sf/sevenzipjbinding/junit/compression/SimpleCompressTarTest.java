package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchiveTar;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackTar;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.CallbackTester;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Test;


/**
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public class SimpleCompressTarTest extends JUnitNativeTestBase {
    private class OutCreateArchiveTar implements IOutCreateCallback<IOutItemCallbackTar> {

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long complete) throws SevenZipException {
        }

        public ISequentialInStream getStream(int index) {
            ByteArrayStream byteArrayStream = virtualContent.getItemStream(index);
            byteArrayStream.rewind();
            return byteArrayStream;
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItemCallbackTar getOutItemCallback(final int index) throws SevenZipException {
            return new IOutItemCallbackTar() {
                public Integer getPosixAttributes() throws SevenZipException {
                    return null;
                }

                public String getPath() throws SevenZipException {
                    return virtualContent.getItemPath(index);
                }

                public Date getModificationTime() throws SevenZipException {
                    return new Date();
                }

                public boolean isDir() throws SevenZipException {
                    return false;
                }

                public String getUser() throws SevenZipException {
                    return "me";
                }

                public String getGroup() throws SevenZipException {
                    return "developers";
                }
            };
        }
    }

    static final Date DATE = new Date();

    VirtualContent virtualContent;
    CallbackTester<OutCreateArchiveTar> callbackTesterCreateArchive = new CallbackTester<OutCreateArchiveTar>(
            new OutCreateArchiveTar());

    @Test
    public void testCompressionTar() throws Exception {
        virtualContent = new VirtualContent(new VirtualContentConfiguration());
        virtualContent.fillRandomly(100, 3, 3, 100, 50, null);

        ByteArrayStream byteArrayStream = new ByteArrayStream(1000000);

        IOutCreateArchiveTar outNewArchiveTar = closeLater(SevenZip.openOutArchiveTar());

        assertEquals(ArchiveFormat.TAR, outNewArchiveTar.getArchiveFormat());

        outNewArchiveTar.createArchive(byteArrayStream, virtualContent.getItemCount(),
                callbackTesterCreateArchive.getInstance());

        assertEquals(5, callbackTesterCreateArchive.getDifferentMethodsCalled());

        byteArrayStream.rewind();

        IInArchive inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.TAR, byteArrayStream));
        virtualContent.verifyInArchive(inArchive);
    }

    Date substructDate(Date date, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }
}
