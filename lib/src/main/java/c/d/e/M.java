package c.d.e;

import static java.lang.System.arraycopy;
import static java.lang.System.out;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.crypto.Cipher.getInstance;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.copyOfRange;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.function.Consumer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;


public class M {

	private static final String A = "AES";
	private static final String TR = "AES/GCM/NoPadding";

	public static void main(String[] a) {
		main(a, t -> out.println(t));
	}

	public static void main(String[] a, Consumer<String> dc) {
        try {
			run(a, dc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void run(String[] a, Consumer<String> dc)
			throws FileNotFoundException, GeneralSecurityException, IOException {
		T o = o(a);
        if (o == null) {
        	throw new IllegalArgumentException("Nothing to do");
        }
		String t = t(a);
		if (o == T.E && (t == null || t.isBlank())) {
			throw new IllegalArgumentException("Nothing to encode");
		}
        File s = s(a);
        if (s != null && !s.exists()) {
        	throw new IllegalArgumentException("No input file");
        }
        File d = d(a);
        if (d == null) {
        	String n = s.getName();
        	int i = n.lastIndexOf('.');
        	if (i > 0) {
        		n = n.substring(0, i);
        	}
        	d = new File(s.getParentFile(), n = n + ".out.png");
        }
        if (o == T.E && d != null && d.exists()) {
        	d.delete();
        }        
        String p = p(a);
        if (T.E == o) {
        	if (p != null) {
        		t = ect(t, p);
        	}
        	if (s == null) {
        		out.println(t);
        	} else {
        		ecd(s, d, t);
        	}
        } else if (T.D == o) {
        	if (s != null) {
        		t = dcd(s);
        	}
        	if (p != null) {
        		t = dct(t, p);
        	}
        	dc.accept(t);
        }
        out.println("$$$ done.");
	}

    private static T o(String[] a) {
    	for (int i = 0; i < a.length; ++i) {
    		if ("-d".equals(a[i])) {
    			return T.D;
    		}
    		if ("-e".equals(a[i])) {
    			return T.E;
    		}
    	}
		return null;
	}

	private static String p(String[] a) {
    	for (int i = 0; i < a.length; ++i) {
    		if ("-p".equals(a[i])) {
    			if (i + 1 >=  a.length) {
    				return null;
    			}
    			String r = a[i + 1];
    			if (r == null || r.isBlank()) {
    				return null;
    			}
    			if (r.trim().charAt(0) == '-') {
    				return null;
    			}
    			return r;
    		}
    	}
		return null;
	}

	private static File d(String[] a) {
    	for (int i = 0; i < a.length; ++i) {
    		if ("-o".equals(a[i])) {
    			if (i + 1 >=  a.length) {
    				return null;
    			}
    			if (a[i + 1].trim().charAt(0) == '-') {
    				return null;
    			}
    			return new File(a[i + 1]);
    		}
    	}
		return null;
	}

	private static File s(String[] a) {
    	for (int i = 0; i < a.length; ++i) {
    		if ("-i".equals(a[i])) {
    			if (i + 1 >=  a.length) {
    				return null;
    			}
    			if (a[i + 1].trim().charAt(0) == '-') {
    				return null;
    			}
    			return new File(a[i + 1]);
    		}
    	}
		return null;
	}

	private static String t(String[] a) throws FileNotFoundException {
    	for (int i = 0; i < a.length; ++i) {
    		if ("-m".equals(a[i])) {
    			if (i + 1 >=  a.length) {
    				return null;
    			}
    			return a[i + 1];
    		}
    		if ("-f".equals(a[i])) {
    			if (i + 1 >=  a.length) {
    				return null;
    			}
    			if (a[i + 1].trim().charAt(0) == '-') {
    				return null;
    			}
    			Scanner s = new Scanner(new File(a[i + 1]));
    			StringBuilder o = new StringBuilder();
    			while (s.hasNextLine()) {
    				o.append(s.nextLine()).append('\n');
    			}
    			s.close();
    			return o.toString();
    		}
    	}
		return null;
	}

    private static byte[] rd(byte[] ib, int dl) {
        byte[] db = new byte[dl];
        int di = 0;
        for (int i = 0; i < ib.length; i += 4) {
            for (int j = 0; j < 3; j++, di++) {
                if (di >= dl * 8) {
                    return db;
                }
                db[di / 8] = (byte) ((db[di / 8] << 1) | (ib[i + j] & 1));
            }
        }
        return db;
    }

    private static String dcd(File s) throws IOException {
        byte[] ib = ((DataBufferByte) read(s).getRaster().getDataBuffer()).getData();
        int ml = wrap(rd(ib, 4)).getInt();
        byte[] am = new byte[ml];
        arraycopy(rd(ib, 4 + ml), 4, am, 0, ml);
        return new String(am, "UTF-8");
    }

    private static void wd(byte[] ib, byte[] db) {
        int dbi = 0;
        for (int i = 0; i < ib.length; i += 4) {
            for (int j = 0; j < 3; j++, dbi++) {
                if (dbi >= db.length * 8) {
                    return;
                }
                ib[i + j] = (byte) ((ib[i + j] & 0xFE) | ((db[dbi / 8] >> (7 - (dbi % 8))) & 1));
            }
        }
    }

    private static void ecd(File s, File d, String t) throws IOException {
        byte[] bt = t.getBytes("UTF-8");
        ByteBuffer lb = allocate(4);
        lb.putInt(bt.length);
        byte[] l = lb.array();
        byte[] bd = new byte[l.length + bt.length];
        arraycopy(l, 0, bd, 0, l.length);
        arraycopy(bt, 0, bd, l.length, bt.length);
        BufferedImage i = read(s);
        wd(((DataBufferByte) i.getRaster().getDataBuffer()).getData(), bd);
        write(i, "png", d);
    }

    private static String ect(String t, String s) throws GeneralSecurityException {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher c = getInstance(TR);
        c.init(ENCRYPT_MODE, gk(s), gs(iv));
        byte[] eb = c.doFinal(t.getBytes(UTF_8));
        byte[] ed = new byte[iv.length + eb.length];
        arraycopy(iv, 0, ed, 0, iv.length);
        arraycopy(eb, 0, ed, iv.length, eb.length);
        return getEncoder().encodeToString(ed);
    }

    private static String dct(String t, String s) throws GeneralSecurityException {
    	byte[] b = getDecoder().decode(t);
        Cipher c = getInstance(TR);
        c.init(DECRYPT_MODE, gk(s), gs(copyOfRange(b, 0, 12)));
        return new String(c.doFinal(copyOfRange(b, 12, b.length)), UTF_8);
    }

    private static SecretKey gk(String s) {
        return new SecretKeySpec(as(s).getBytes(), 0, 16, A);
    }

    private static GCMParameterSpec gs(byte[] iv) {
        return new GCMParameterSpec(128, iv);
    }

    private static String as(String s) {
    	if (s.length() < 16) {
    		StringBuilder o = new StringBuilder(s);
    		for (int i = s.length() - 1; i < 16; ++i) {
    			o.append("$");
    		}
    		s = o.toString();
    	}
    	return s;
    }

    private enum T {
    	D,E;
    }

}
