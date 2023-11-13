package ccm.server.utils;

import ccm.server.zip.entity.ZipInnerFile;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2023/2/10 16:52
 */
@Slf4j
public class FileUtility {

    public static final String directory = "document";
    public static final String directoryPath = "document/";

    public static final Map<String,String> FILE_TYPE = new HashMap<>();

    static {
        FILE_TYPE.put(".load" , "text/html");
        FILE_TYPE.put(".123" , "application/vnd.lotus-1-2-3");
        FILE_TYPE.put(".3ds" , "image/x-3ds");
        FILE_TYPE.put(".3g2" , "video/3gpp");
        FILE_TYPE.put(".3ga" , "video/3gpp");
        FILE_TYPE.put(".3gp" , "video/3gpp");
        FILE_TYPE.put(".3gpp" , "video/3gpp");
        FILE_TYPE.put(".602" , "application/x-t602");
        FILE_TYPE.put(".669" , "audio/x-mod");
        FILE_TYPE.put(".7z" , "application/x-7z-compressed");
        FILE_TYPE.put(".a" , "application/x-archive");
        FILE_TYPE.put(".aac" , "audio/mp4");
        FILE_TYPE.put(".abw" , "application/x-abiword");
        FILE_TYPE.put(".abw.crashed" , "application/x-abiword");
        FILE_TYPE.put(".abw.gz" , "application/x-abiword");
        FILE_TYPE.put(".ac3" , "audio/ac3");
        FILE_TYPE.put(".ace" , "application/x-ace");
        FILE_TYPE.put(".adb" , "text/x-adasrc");
        FILE_TYPE.put(".ads" , "text/x-adasrc");
        FILE_TYPE.put(".afm" , "application/x-font-afm");
        FILE_TYPE.put(".ag" , "image/x-applix-graphics");
        FILE_TYPE.put(".ai" , "application/illustrator");
        FILE_TYPE.put(".aif" , "audio/x-aiff");
        FILE_TYPE.put(".aifc" , "audio/x-aiff");
        FILE_TYPE.put(".aiff" , "audio/x-aiff");
        FILE_TYPE.put(".al" , "application/x-perl");
        FILE_TYPE.put(".alz" , "application/x-alz");
        FILE_TYPE.put(".amr" , "audio/amr");
        FILE_TYPE.put(".ani" , "application/x-navi-animation");
        FILE_TYPE.put(".anim[1-9j]" , "video/x-anim");
        FILE_TYPE.put(".anx" , "application/annodex");
        FILE_TYPE.put(".ape" , "audio/x-ape");
        FILE_TYPE.put(".arj" , "application/x-arj");
        FILE_TYPE.put(".arw" , "image/x-sony-arw");
        FILE_TYPE.put(".as" , "application/x-applix-spreadsheet");
        FILE_TYPE.put(".asc" , "text/plain");
        FILE_TYPE.put(".asf" , "video/x-ms-asf");
        FILE_TYPE.put(".asp" , "application/x-asp");
        FILE_TYPE.put(".ass" , "text/x-ssa");
        FILE_TYPE.put(".asx" , "audio/x-ms-asx");
        FILE_TYPE.put(".atom" , "application/atom+xml");
        FILE_TYPE.put(".au" , "audio/basic");
        FILE_TYPE.put(".avi" , "video/x-msvideo");
        FILE_TYPE.put(".aw" , "application/x-applix-word");
        FILE_TYPE.put(".awb" , "audio/amr-wb");
        FILE_TYPE.put(".awk" , "application/x-awk");
        FILE_TYPE.put(".axa" , "audio/annodex");
        FILE_TYPE.put(".axv" , "video/annodex");
        FILE_TYPE.put(".bak" , "application/x-trash");
        FILE_TYPE.put(".bcpio" , "application/x-bcpio");
        FILE_TYPE.put(".bdf" , "application/x-font-bdf");
        FILE_TYPE.put(".bib" , "text/x-bibtex");
        FILE_TYPE.put(".bin" , "application/octet-stream");
        FILE_TYPE.put(".blend" , "application/x-blender");
        FILE_TYPE.put(".blender" , "application/x-blender");
        FILE_TYPE.put(".bmp" , "image/bmp");
        FILE_TYPE.put(".bz" , "application/x-bzip");
        FILE_TYPE.put(".bz2" , "application/x-bzip");
        FILE_TYPE.put(".c" , "text/x-csrc");
        FILE_TYPE.put(".c++" , "text/x-c++src");
        FILE_TYPE.put(".cab" , "application/vnd.ms-cab-compressed");
        FILE_TYPE.put(".cb7" , "application/x-cb7");
        FILE_TYPE.put(".cbr" , "application/x-cbr");
        FILE_TYPE.put(".cbt" , "application/x-cbt");
        FILE_TYPE.put(".cbz" , "application/x-cbz");
        FILE_TYPE.put(".cc" , "text/x-c++src");
        FILE_TYPE.put(".cdf" , "application/x-netcdf");
        FILE_TYPE.put(".cdr" , "application/vnd.corel-draw");
        FILE_TYPE.put(".cer" , "application/x-x509-ca-cert");
        FILE_TYPE.put(".cert" , "application/x-x509-ca-cert");
        FILE_TYPE.put(".cgm" , "image/cgm");
        FILE_TYPE.put(".chm" , "application/x-chm");
        FILE_TYPE.put(".chrt" , "application/x-kchart");
        FILE_TYPE.put(".class" , "application/x-java");
        FILE_TYPE.put(".cls" , "text/x-tex");
        FILE_TYPE.put(".cmake" , "text/x-cmake");
        FILE_TYPE.put(".cpio" , "application/x-cpio");
        FILE_TYPE.put(".cpio.gz" , "application/x-cpio-compressed");
        FILE_TYPE.put(".cpp" , "text/x-c++src");
        FILE_TYPE.put(".cr2" , "image/x-canon-cr2");
        FILE_TYPE.put(".crt" , "application/x-x509-ca-cert");
        FILE_TYPE.put(".crw" , "image/x-canon-crw");
        FILE_TYPE.put(".cs" , "text/x-csharp");
        FILE_TYPE.put(".csh" , "application/x-csh");
        FILE_TYPE.put(".css" , "text/css");
        FILE_TYPE.put(".cssl" , "text/css");
        FILE_TYPE.put(".csv" , "text/csv");
        FILE_TYPE.put(".cue" , "application/x-cue");
        FILE_TYPE.put(".cur" , "image/x-win-bitmap");
        FILE_TYPE.put(".cxx" , "text/x-c++src");
        FILE_TYPE.put(".d" , "text/x-dsrc");
        FILE_TYPE.put(".dar" , "application/x-dar");
        FILE_TYPE.put(".dbf" , "application/x-dbf");
        FILE_TYPE.put(".dc" , "application/x-dc-rom");
        FILE_TYPE.put(".dcl" , "text/x-dcl");
        FILE_TYPE.put(".dcm" , "application/dicom");
        FILE_TYPE.put(".dcr" , "image/x-kodak-dcr");
        FILE_TYPE.put(".dds" , "image/x-dds");
        FILE_TYPE.put(".deb" , "application/x-deb");
        FILE_TYPE.put(".der" , "application/x-x509-ca-cert");
        FILE_TYPE.put(".desktop" , "application/x-desktop");
        FILE_TYPE.put(".dia" , "application/x-dia-diagram");
        FILE_TYPE.put(".diff" , "text/x-patch");
        FILE_TYPE.put(".divx" , "video/x-msvideo");
        FILE_TYPE.put(".djv" , "image/vnd.djvu");
        FILE_TYPE.put(".djvu" , "image/vnd.djvu");
        FILE_TYPE.put(".dng" , "image/x-adobe-dng");
        FILE_TYPE.put(".doc" , "application/msword");
        FILE_TYPE.put(".docbook" , "application/docbook+xml");
        FILE_TYPE.put(".docm" , "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        FILE_TYPE.put(".docx" , "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        FILE_TYPE.put(".dot" , "text/vnd.graphviz");
        FILE_TYPE.put(".dsl" , "text/x-dsl");
        FILE_TYPE.put(".dtd" , "application/xml-dtd");
        FILE_TYPE.put(".dtx" , "text/x-tex");
        FILE_TYPE.put(".dv" , "video/dv");
        FILE_TYPE.put(".dvi" , "application/x-dvi");
        FILE_TYPE.put(".dvi.bz2" , "application/x-bzdvi");
        FILE_TYPE.put(".dvi.gz" , "application/x-gzdvi");
        FILE_TYPE.put(".dwg" , "image/vnd.dwg");
        FILE_TYPE.put(".dxf" , "image/vnd.dxf");
        FILE_TYPE.put(".e" , "text/x-eiffel");
        FILE_TYPE.put(".egon" , "application/x-egon");
        FILE_TYPE.put(".eif" , "text/x-eiffel");
        FILE_TYPE.put(".el" , "text/x-emacs-lisp");
        FILE_TYPE.put(".emf" , "image/x-emf");
        FILE_TYPE.put(".emp" , "application/vnd.emusic-emusic_package");
        FILE_TYPE.put(".ent" , "application/xml-external-parsed-entity");
        FILE_TYPE.put(".eps" , "image/x-eps");
        FILE_TYPE.put(".eps.bz2" , "image/x-bzeps");
        FILE_TYPE.put(".eps.gz" , "image/x-gzeps");
        FILE_TYPE.put(".epsf" , "image/x-eps");
        FILE_TYPE.put(".epsf.bz2" , "image/x-bzeps");
        FILE_TYPE.put(".epsf.gz" , "image/x-gzeps");
        FILE_TYPE.put(".epsi" , "image/x-eps");
        FILE_TYPE.put(".epsi.bz2" , "image/x-bzeps");
        FILE_TYPE.put(".epsi.gz" , "image/x-gzeps");
        FILE_TYPE.put(".epub" , "application/epub+zip");
        FILE_TYPE.put(".erl" , "text/x-erlang");
        FILE_TYPE.put(".es" , "application/ecmascript");
        FILE_TYPE.put(".etheme" , "application/x-e-theme");
        FILE_TYPE.put(".etx" , "text/x-setext");
        FILE_TYPE.put(".exe" , "application/x-ms-dos-executable");
        FILE_TYPE.put(".exr" , "image/x-exr");
        FILE_TYPE.put(".ez" , "application/andrew-inset");
        FILE_TYPE.put(".f" , "text/x-fortran");
        FILE_TYPE.put(".f90" , "text/x-fortran");
        FILE_TYPE.put(".f95" , "text/x-fortran");
        FILE_TYPE.put(".fb2" , "application/x-fictionbook+xml");
        FILE_TYPE.put(".fig" , "image/x-xfig");
        FILE_TYPE.put(".fits" , "image/fits");
        FILE_TYPE.put(".fl" , "application/x-fluid");
        FILE_TYPE.put(".flac" , "audio/x-flac");
        FILE_TYPE.put(".flc" , "video/x-flic");
        FILE_TYPE.put(".fli" , "video/x-flic");
        FILE_TYPE.put(".flv" , "video/x-flv");
        FILE_TYPE.put(".flw" , "application/x-kivio");
        FILE_TYPE.put(".fo" , "text/x-xslfo");
        FILE_TYPE.put(".for" , "text/x-fortran");
        FILE_TYPE.put(".g3" , "image/fax-g3");
        FILE_TYPE.put(".gb" , "application/x-gameboy-rom");
        FILE_TYPE.put(".gba" , "application/x-gba-rom");
        FILE_TYPE.put(".gcrd" , "text/directory");
        FILE_TYPE.put(".ged" , "application/x-gedcom");
        FILE_TYPE.put(".gedcom" , "application/x-gedcom");
        FILE_TYPE.put(".gen" , "application/x-genesis-rom");
        FILE_TYPE.put(".gf" , "application/x-tex-gf");
        FILE_TYPE.put(".gg" , "application/x-sms-rom");
        FILE_TYPE.put(".gif" , "image/gif");
        FILE_TYPE.put(".glade" , "application/x-glade");
        FILE_TYPE.put(".gmo" , "application/x-gettext-translation");
        FILE_TYPE.put(".gnc" , "application/x-gnucash");
        FILE_TYPE.put(".gnd" , "application/gnunet-directory");
        FILE_TYPE.put(".gnucash" , "application/x-gnucash");
        FILE_TYPE.put(".gnumeric" , "application/x-gnumeric");
        FILE_TYPE.put(".gnuplot" , "application/x-gnuplot");
        FILE_TYPE.put(".gp" , "application/x-gnuplot");
        FILE_TYPE.put(".gpg" , "application/pgp-encrypted");
        FILE_TYPE.put(".gplt" , "application/x-gnuplot");
        FILE_TYPE.put(".gra" , "application/x-graphite");
        FILE_TYPE.put(".gsf" , "application/x-font-type1");
        FILE_TYPE.put(".gsm" , "audio/x-gsm");
        FILE_TYPE.put(".gtar" , "application/x-tar");
        FILE_TYPE.put(".gv" , "text/vnd.graphviz");
        FILE_TYPE.put(".gvp" , "text/x-google-video-pointer");
        FILE_TYPE.put(".gz" , "application/x-gzip");
        FILE_TYPE.put(".h" , "text/x-chdr");
        FILE_TYPE.put(".h++" , "text/x-c++hdr");
        FILE_TYPE.put(".hdf" , "application/x-hdf");
        FILE_TYPE.put(".hh" , "text/x-c++hdr");
        FILE_TYPE.put(".hp" , "text/x-c++hdr");
        FILE_TYPE.put(".hpgl" , "application/vnd.hp-hpgl");
        FILE_TYPE.put(".hpp" , "text/x-c++hdr");
        FILE_TYPE.put(".hs" , "text/x-haskell");
        FILE_TYPE.put(".htm" , "text/html");
        FILE_TYPE.put(".html" , "text/html");
        FILE_TYPE.put(".hwp" , "application/x-hwp");
        FILE_TYPE.put(".hwt" , "application/x-hwt");
        FILE_TYPE.put(".hxx" , "text/x-c++hdr");
        FILE_TYPE.put(".ica" , "application/x-ica");
        FILE_TYPE.put(".icb" , "image/x-tga");
        FILE_TYPE.put(".icns" , "image/x-icns");
        FILE_TYPE.put(".ico" , "image/vnd.microsoft.icon");
        FILE_TYPE.put(".ics" , "text/calendar");
        FILE_TYPE.put(".idl" , "text/x-idl");
        FILE_TYPE.put(".ief" , "image/ief");
        FILE_TYPE.put(".iff" , "image/x-iff");
        FILE_TYPE.put(".ilbm" , "image/x-ilbm");
        FILE_TYPE.put(".ime" , "text/x-imelody");
        FILE_TYPE.put(".imy" , "text/x-imelody");
        FILE_TYPE.put(".ins" , "text/x-tex");
        FILE_TYPE.put(".iptables" , "text/x-iptables");
        FILE_TYPE.put(".iso" , "application/x-cd-image");
        FILE_TYPE.put(".iso9660" , "application/x-cd-image");
        FILE_TYPE.put(".it" , "audio/x-it");
        FILE_TYPE.put(".j2k" , "image/jp2");
        FILE_TYPE.put(".jad" , "text/vnd.sun.j2me.app-descriptor");
        FILE_TYPE.put(".jar" , "application/x-java-archive");
        FILE_TYPE.put(".java" , "text/x-java");
        FILE_TYPE.put(".jng" , "image/x-jng");
        FILE_TYPE.put(".jnlp" , "application/x-java-jnlp-file");
        FILE_TYPE.put(".jp2" , "image/jp2");
        FILE_TYPE.put(".jpc" , "image/jp2");
        FILE_TYPE.put(".jpe" , "image/jpeg");
        FILE_TYPE.put(".jpeg" , "image/jpeg");
        FILE_TYPE.put(".jpf" , "image/jp2");
        FILE_TYPE.put(".jpg" , "image/jpeg");
        FILE_TYPE.put(".jpr" , "application/x-jbuilder-project");
        FILE_TYPE.put(".jpx" , "image/jp2");
        FILE_TYPE.put(".js" , "application/javascript");
        FILE_TYPE.put(".json" , "application/json");
        FILE_TYPE.put(".jsonp" , "application/jsonp");
        FILE_TYPE.put(".k25" , "image/x-kodak-k25");
        FILE_TYPE.put(".kar" , "audio/midi");
        FILE_TYPE.put(".karbon" , "application/x-karbon");
        FILE_TYPE.put(".kdc" , "image/x-kodak-kdc");
        FILE_TYPE.put(".kdelnk" , "application/x-desktop");
        FILE_TYPE.put(".kexi" , "application/x-kexiproject-sqlite3");
        FILE_TYPE.put(".kexic" , "application/x-kexi-connectiondata");
        FILE_TYPE.put(".kexis" , "application/x-kexiproject-shortcut");
        FILE_TYPE.put(".kfo" , "application/x-kformula");
        FILE_TYPE.put(".kil" , "application/x-killustrator");
        FILE_TYPE.put(".kino" , "application/smil");
        FILE_TYPE.put(".kml" , "application/vnd.google-earth.kml+xml");
        FILE_TYPE.put(".kmz" , "application/vnd.google-earth.kmz");
        FILE_TYPE.put(".kon" , "application/x-kontour");
        FILE_TYPE.put(".kpm" , "application/x-kpovmodeler");
        FILE_TYPE.put(".kpr" , "application/x-kpresenter");
        FILE_TYPE.put(".kpt" , "application/x-kpresenter");
        FILE_TYPE.put(".kra" , "application/x-krita");
        FILE_TYPE.put(".ksp" , "application/x-kspread");
        FILE_TYPE.put(".kud" , "application/x-kugar");
        FILE_TYPE.put(".kwd" , "application/x-kword");
        FILE_TYPE.put(".kwt" , "application/x-kword");
        FILE_TYPE.put(".la" , "application/x-shared-library-la");
        FILE_TYPE.put(".latex" , "text/x-tex");
        FILE_TYPE.put(".ldif" , "text/x-ldif");
        FILE_TYPE.put(".lha" , "application/x-lha");
        FILE_TYPE.put(".lhs" , "text/x-literate-haskell");
        FILE_TYPE.put(".lhz" , "application/x-lhz");
        FILE_TYPE.put(".log" , "text/x-log");
        FILE_TYPE.put(".ltx" , "text/x-tex");
        FILE_TYPE.put(".lua" , "text/x-lua");
        FILE_TYPE.put(".lwo" , "image/x-lwo");
        FILE_TYPE.put(".lwob" , "image/x-lwo");
        FILE_TYPE.put(".lws" , "image/x-lws");
        FILE_TYPE.put(".ly" , "text/x-lilypond");
        FILE_TYPE.put(".lyx" , "application/x-lyx");
        FILE_TYPE.put(".lz" , "application/x-lzip");
        FILE_TYPE.put(".lzh" , "application/x-lha");
        FILE_TYPE.put(".lzma" , "application/x-lzma");
        FILE_TYPE.put(".lzo" , "application/x-lzop");
        FILE_TYPE.put(".m" , "text/x-matlab");
        FILE_TYPE.put(".m15" , "audio/x-mod");
        FILE_TYPE.put(".m2t" , "video/mpeg");
        FILE_TYPE.put(".m3u" , "audio/x-mpegurl");
        FILE_TYPE.put(".m3u8" , "audio/x-mpegurl");
        FILE_TYPE.put(".m4" , "application/x-m4");
        FILE_TYPE.put(".m4a" , "audio/mp4");
        FILE_TYPE.put(".m4b" , "audio/x-m4b");
        FILE_TYPE.put(".m4v" , "video/mp4");
        FILE_TYPE.put(".mab" , "application/x-markaby");
        FILE_TYPE.put(".man" , "application/x-troff-man");
        FILE_TYPE.put(".mbox" , "application/mbox");
        FILE_TYPE.put(".md" , "application/x-genesis-rom");
        FILE_TYPE.put(".mdb" , "application/vnd.ms-access");
        FILE_TYPE.put(".mdi" , "image/vnd.ms-modi");
        FILE_TYPE.put(".me" , "text/x-troff-me");
        FILE_TYPE.put(".med" , "audio/x-mod");
        FILE_TYPE.put(".metalink" , "application/metalink+xml");
        FILE_TYPE.put(".mgp" , "application/x-magicpoint");
        FILE_TYPE.put(".mid" , "audio/midi");
        FILE_TYPE.put(".midi" , "audio/midi");
        FILE_TYPE.put(".mif" , "application/x-mif");
        FILE_TYPE.put(".minipsf" , "audio/x-minipsf");
        FILE_TYPE.put(".mka" , "audio/x-matroska");
        FILE_TYPE.put(".mkv" , "video/x-matroska");
        FILE_TYPE.put(".ml" , "text/x-ocaml");
        FILE_TYPE.put(".mli" , "text/x-ocaml");
        FILE_TYPE.put(".mm" , "text/x-troff-mm");
        FILE_TYPE.put(".mmf" , "application/x-smaf");
        FILE_TYPE.put(".mml" , "text/mathml");
        FILE_TYPE.put(".mng" , "video/x-mng");
        FILE_TYPE.put(".mo" , "application/x-gettext-translation");
        FILE_TYPE.put(".mo3" , "audio/x-mo3");
        FILE_TYPE.put(".moc" , "text/x-moc");
        FILE_TYPE.put(".mod" , "audio/x-mod");
        FILE_TYPE.put(".mof" , "text/x-mof");
        FILE_TYPE.put(".moov" , "video/quicktime");
        FILE_TYPE.put(".mov" , "video/quicktime");
        FILE_TYPE.put(".movie" , "video/x-sgi-movie");
        FILE_TYPE.put(".mp+" , "audio/x-musepack");
        FILE_TYPE.put(".mp2" , "video/mpeg");
        FILE_TYPE.put(".mp3" , "audio/mpeg");
        FILE_TYPE.put(".mp4" , "video/mp4");
        FILE_TYPE.put(".mpc" , "audio/x-musepack");
        FILE_TYPE.put(".mpe" , "video/mpeg");
        FILE_TYPE.put(".mpeg" , "video/mpeg");
        FILE_TYPE.put(".mpg" , "video/mpeg");
        FILE_TYPE.put(".mpga" , "audio/mpeg");
        FILE_TYPE.put(".mpp" , "audio/x-musepack");
        FILE_TYPE.put(".mrl" , "text/x-mrml");
        FILE_TYPE.put(".mrml" , "text/x-mrml");
        FILE_TYPE.put(".mrw" , "image/x-minolta-mrw");
        FILE_TYPE.put(".ms" , "text/x-troff-ms");
        FILE_TYPE.put(".msi" , "application/x-msi");
        FILE_TYPE.put(".msod" , "image/x-msod");
        FILE_TYPE.put(".msx" , "application/x-msx-rom");
        FILE_TYPE.put(".mtm" , "audio/x-mod");
        FILE_TYPE.put(".mup" , "text/x-mup");
        FILE_TYPE.put(".mxf" , "application/mxf");
        FILE_TYPE.put(".n64" , "application/x-n64-rom");
        FILE_TYPE.put(".nb" , "application/mathematica");
        FILE_TYPE.put(".nc" , "application/x-netcdf");
        FILE_TYPE.put(".nds" , "application/x-nintendo-ds-rom");
        FILE_TYPE.put(".nef" , "image/x-nikon-nef");
        FILE_TYPE.put(".nes" , "application/x-nes-rom");
        FILE_TYPE.put(".nfo" , "text/x-nfo");
        FILE_TYPE.put(".not" , "text/x-mup");
        FILE_TYPE.put(".nsc" , "application/x-netshow-channel");
        FILE_TYPE.put(".nsv" , "video/x-nsv");
        FILE_TYPE.put(".o" , "application/x-object");
        FILE_TYPE.put(".obj" , "application/x-tgif");
        FILE_TYPE.put(".ocl" , "text/x-ocl");
        FILE_TYPE.put(".oda" , "application/oda");
        FILE_TYPE.put(".odb" , "application/vnd.oasis.opendocument.database");
        FILE_TYPE.put(".odc" , "application/vnd.oasis.opendocument.chart");
        FILE_TYPE.put(".odf" , "application/vnd.oasis.opendocument.formula");
        FILE_TYPE.put(".odg" , "application/vnd.oasis.opendocument.graphics");
        FILE_TYPE.put(".odi" , "application/vnd.oasis.opendocument.image");
        FILE_TYPE.put(".odm" , "application/vnd.oasis.opendocument.text-master");
        FILE_TYPE.put(".odp" , "application/vnd.oasis.opendocument.presentation");
        FILE_TYPE.put(".ods" , "application/vnd.oasis.opendocument.spreadsheet");
        FILE_TYPE.put(".odt" , "application/vnd.oasis.opendocument.text");
        FILE_TYPE.put(".oga" , "audio/ogg");
        FILE_TYPE.put(".ogg" , "video/x-theora+ogg");
        FILE_TYPE.put(".ogm" , "video/x-ogm+ogg");
        FILE_TYPE.put(".ogv" , "video/ogg");
        FILE_TYPE.put(".ogx" , "application/ogg");
        FILE_TYPE.put(".old" , "application/x-trash");
        FILE_TYPE.put(".oleo" , "application/x-oleo");
        FILE_TYPE.put(".opml" , "text/x-opml+xml");
        FILE_TYPE.put(".ora" , "image/openraster");
        FILE_TYPE.put(".orf" , "image/x-olympus-orf");
        FILE_TYPE.put(".otc" , "application/vnd.oasis.opendocument.chart-template");
        FILE_TYPE.put(".otf" , "application/x-font-otf");
        FILE_TYPE.put(".otg" , "application/vnd.oasis.opendocument.graphics-template");
        FILE_TYPE.put(".oth" , "application/vnd.oasis.opendocument.text-web");
        FILE_TYPE.put(".otp" , "application/vnd.oasis.opendocument.presentation-template");
        FILE_TYPE.put(".ots" , "application/vnd.oasis.opendocument.spreadsheet-template");
        FILE_TYPE.put(".ott" , "application/vnd.oasis.opendocument.text-template");
        FILE_TYPE.put(".owl" , "application/rdf+xml");
        FILE_TYPE.put(".oxt" , "application/vnd.openofficeorg.extension");
        FILE_TYPE.put(".p" , "text/x-pascal");
        FILE_TYPE.put(".p10" , "application/pkcs10");
        FILE_TYPE.put(".p12" , "application/x-pkcs12");
        FILE_TYPE.put(".p7b" , "application/x-pkcs7-certificates");
        FILE_TYPE.put(".p7s" , "application/pkcs7-signature");
        FILE_TYPE.put(".pack" , "application/x-java-pack200");
        FILE_TYPE.put(".pak" , "application/x-pak");
        FILE_TYPE.put(".par2" , "application/x-par2");
        FILE_TYPE.put(".pas" , "text/x-pascal");
        FILE_TYPE.put(".patch" , "text/x-patch");
        FILE_TYPE.put(".pbm" , "image/x-portable-bitmap");
        FILE_TYPE.put(".pcd" , "image/x-photo-cd");
        FILE_TYPE.put(".pcf" , "application/x-cisco-vpn-settings");
        FILE_TYPE.put(".pcf.gz" , "application/x-font-pcf");
        FILE_TYPE.put(".pcf.z" , "application/x-font-pcf");
        FILE_TYPE.put(".pcl" , "application/vnd.hp-pcl");
        FILE_TYPE.put(".pcx" , "image/x-pcx");
        FILE_TYPE.put(".pdb" , "chemical/x-pdb");
        FILE_TYPE.put(".pdc" , "application/x-aportisdoc");
        FILE_TYPE.put(".pdf" , "application/pdf");
        FILE_TYPE.put(".pdf.bz2" , "application/x-bzpdf");
        FILE_TYPE.put(".pdf.gz" , "application/x-gzpdf");
        FILE_TYPE.put(".pef" , "image/x-pentax-pef");
        FILE_TYPE.put(".pem" , "application/x-x509-ca-cert");
        FILE_TYPE.put(".perl" , "application/x-perl");
        FILE_TYPE.put(".pfa" , "application/x-font-type1");
        FILE_TYPE.put(".pfb" , "application/x-font-type1");
        FILE_TYPE.put(".pfx" , "application/x-pkcs12");
        FILE_TYPE.put(".pgm" , "image/x-portable-graymap");
        FILE_TYPE.put(".pgn" , "application/x-chess-pgn");
        FILE_TYPE.put(".pgp" , "application/pgp-encrypted");
        FILE_TYPE.put(".php" , "application/x-php");
        FILE_TYPE.put(".php3" , "application/x-php");
        FILE_TYPE.put(".php4" , "application/x-php");
        FILE_TYPE.put(".pict" , "image/x-pict");
        FILE_TYPE.put(".pict1" , "image/x-pict");
        FILE_TYPE.put(".pict2" , "image/x-pict");
        FILE_TYPE.put(".pickle" , "application/python-pickle");
        FILE_TYPE.put(".pk" , "application/x-tex-pk");
        FILE_TYPE.put(".pkipath" , "application/pkix-pkipath");
        FILE_TYPE.put(".pkr" , "application/pgp-keys");
        FILE_TYPE.put(".pl" , "application/x-perl");
        FILE_TYPE.put(".pla" , "audio/x-iriver-pla");
        FILE_TYPE.put(".pln" , "application/x-planperfect");
        FILE_TYPE.put(".pls" , "audio/x-scpls");
        FILE_TYPE.put(".pm" , "application/x-perl");
        FILE_TYPE.put(".png" , "image/png");
        FILE_TYPE.put(".pnm" , "image/x-portable-anymap");
        FILE_TYPE.put(".pntg" , "image/x-macpaint");
        FILE_TYPE.put(".po" , "text/x-gettext-translation");
        FILE_TYPE.put(".por" , "application/x-spss-por");
        FILE_TYPE.put(".pot" , "text/x-gettext-translation-template");
        FILE_TYPE.put(".ppm" , "image/x-portable-pixmap");
        FILE_TYPE.put(".pps" , "application/vnd.ms-powerpoint");
        FILE_TYPE.put(".ppt" , "application/vnd.ms-powerpoint");
        FILE_TYPE.put(".pptm" , "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        FILE_TYPE.put(".pptx" , "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        FILE_TYPE.put(".ppz" , "application/vnd.ms-powerpoint");
        FILE_TYPE.put(".prc" , "application/x-palm-database");
        FILE_TYPE.put(".ps" , "application/postscript");
        FILE_TYPE.put(".ps.bz2" , "application/x-bzpostscript");
        FILE_TYPE.put(".ps.gz" , "application/x-gzpostscript");
        FILE_TYPE.put(".psd" , "image/vnd.adobe.photoshop");
        FILE_TYPE.put(".psf" , "audio/x-psf");
        FILE_TYPE.put(".psf.gz" , "application/x-gz-font-linux-psf");
        FILE_TYPE.put(".psflib" , "audio/x-psflib");
        FILE_TYPE.put(".psid" , "audio/prs.sid");
        FILE_TYPE.put(".psw" , "application/x-pocket-word");
        FILE_TYPE.put(".pw" , "application/x-pw");
        FILE_TYPE.put(".py" , "text/x-python");
        FILE_TYPE.put(".pyc" , "application/x-python-bytecode");
        FILE_TYPE.put(".pyo" , "application/x-python-bytecode");
        FILE_TYPE.put(".qif" , "image/x-quicktime");
        FILE_TYPE.put(".qt" , "video/quicktime");
        FILE_TYPE.put(".qtif" , "image/x-quicktime");
        FILE_TYPE.put(".qtl" , "application/x-quicktime-media-link");
        FILE_TYPE.put(".qtvr" , "video/quicktime");
        FILE_TYPE.put(".ra" , "audio/vnd.rn-realaudio");
        FILE_TYPE.put(".raf" , "image/x-fuji-raf");
        FILE_TYPE.put(".ram" , "application/ram");
        FILE_TYPE.put(".rar" , "application/x-rar");
        FILE_TYPE.put(".ras" , "image/x-cmu-raster");
        FILE_TYPE.put(".raw" , "image/x-panasonic-raw");
        FILE_TYPE.put(".rax" , "audio/vnd.rn-realaudio");
        FILE_TYPE.put(".rb" , "application/x-ruby");
        FILE_TYPE.put(".rdf" , "application/rdf+xml");
        FILE_TYPE.put(".rdfs" , "application/rdf+xml");
        FILE_TYPE.put(".reg" , "text/x-ms-regedit");
        FILE_TYPE.put(".rej" , "application/x-reject");
        FILE_TYPE.put(".rgb" , "image/x-rgb");
        FILE_TYPE.put(".rle" , "image/rle");
        FILE_TYPE.put(".rm" , "application/vnd.rn-realmedia");
        FILE_TYPE.put(".rmj" , "application/vnd.rn-realmedia");
        FILE_TYPE.put(".rmm" , "application/vnd.rn-realmedia");
        FILE_TYPE.put(".rms" , "application/vnd.rn-realmedia");
        FILE_TYPE.put(".rmvb" , "application/vnd.rn-realmedia");
        FILE_TYPE.put(".rmx" , "application/vnd.rn-realmedia");
        FILE_TYPE.put(".roff" , "text/troff");
        FILE_TYPE.put(".rp" , "image/vnd.rn-realpix");
        FILE_TYPE.put(".rpm" , "application/x-rpm");
        FILE_TYPE.put(".rss" , "application/rss+xml");
        FILE_TYPE.put(".rt" , "text/vnd.rn-realtext");
        FILE_TYPE.put(".rtf" , "application/rtf");
        FILE_TYPE.put(".rtx" , "text/richtext");
        FILE_TYPE.put(".rv" , "video/vnd.rn-realvideo");
        FILE_TYPE.put(".rvx" , "video/vnd.rn-realvideo");
        FILE_TYPE.put(".s3m" , "audio/x-s3m");
        FILE_TYPE.put(".sam" , "application/x-amipro");
        FILE_TYPE.put(".sami" , "application/x-sami");
        FILE_TYPE.put(".sav" , "application/x-spss-sav");
        FILE_TYPE.put(".scm" , "text/x-scheme");
        FILE_TYPE.put(".sda" , "application/vnd.stardivision.draw");
        FILE_TYPE.put(".sdc" , "application/vnd.stardivision.calc");
        FILE_TYPE.put(".sdd" , "application/vnd.stardivision.impress");
        FILE_TYPE.put(".sdp" , "application/sdp");
        FILE_TYPE.put(".sds" , "application/vnd.stardivision.chart");
        FILE_TYPE.put(".sdw" , "application/vnd.stardivision.writer");
        FILE_TYPE.put(".sgf" , "application/x-go-sgf");
        FILE_TYPE.put(".sgi" , "image/x-sgi");
        FILE_TYPE.put(".sgl" , "application/vnd.stardivision.writer");
        FILE_TYPE.put(".sgm" , "text/sgml");
        FILE_TYPE.put(".sgml" , "text/sgml");
        FILE_TYPE.put(".sh" , "application/x-shellscript");
        FILE_TYPE.put(".shar" , "application/x-shar");
        FILE_TYPE.put(".shn" , "application/x-shorten");
        FILE_TYPE.put(".siag" , "application/x-siag");
        FILE_TYPE.put(".sid" , "audio/prs.sid");
        FILE_TYPE.put(".sik" , "application/x-trash");
        FILE_TYPE.put(".sis" , "application/vnd.symbian.install");
        FILE_TYPE.put(".sisx" , "x-epoc/x-sisx-app");
        FILE_TYPE.put(".sit" , "application/x-stuffit");
        FILE_TYPE.put(".siv" , "application/sieve");
        FILE_TYPE.put(".sk" , "image/x-skencil");
        FILE_TYPE.put(".sk1" , "image/x-skencil");
        FILE_TYPE.put(".skr" , "application/pgp-keys");
        FILE_TYPE.put(".slk" , "text/spreadsheet");
        FILE_TYPE.put(".smaf" , "application/x-smaf");
        FILE_TYPE.put(".smc" , "application/x-snes-rom");
        FILE_TYPE.put(".smd" , "application/vnd.stardivision.mail");
        FILE_TYPE.put(".smf" , "application/vnd.stardivision.math");
        FILE_TYPE.put(".smi" , "application/x-sami");
        FILE_TYPE.put(".smil" , "application/smil");
        FILE_TYPE.put(".sml" , "application/smil");
        FILE_TYPE.put(".sms" , "application/x-sms-rom");
        FILE_TYPE.put(".snd" , "audio/basic");
        FILE_TYPE.put(".so" , "application/x-sharedlib");
        FILE_TYPE.put(".spc" , "application/x-pkcs7-certificates");
        FILE_TYPE.put(".spd" , "application/x-font-speedo");
        FILE_TYPE.put(".spec" , "text/x-rpm-spec");
        FILE_TYPE.put(".spl" , "application/x-shockwave-flash");
        FILE_TYPE.put(".spx" , "audio/x-speex");
        FILE_TYPE.put(".sql" , "text/x-sql");
        FILE_TYPE.put(".sr2" , "image/x-sony-sr2");
        FILE_TYPE.put(".src" , "application/x-wais-source");
        FILE_TYPE.put(".srf" , "image/x-sony-srf");
        FILE_TYPE.put(".srt" , "application/x-subrip");
        FILE_TYPE.put(".ssa" , "text/x-ssa");
        FILE_TYPE.put(".stc" , "application/vnd.sun.xml.calc.template");
        FILE_TYPE.put(".std" , "application/vnd.sun.xml.draw.template");
        FILE_TYPE.put(".sti" , "application/vnd.sun.xml.impress.template");
        FILE_TYPE.put(".stm" , "audio/x-stm");
        FILE_TYPE.put(".stw" , "application/vnd.sun.xml.writer.template");
        FILE_TYPE.put(".sty" , "text/x-tex");
        FILE_TYPE.put(".sub" , "text/x-subviewer");
        FILE_TYPE.put(".sun" , "image/x-sun-raster");
        FILE_TYPE.put(".sv4cpio" , "application/x-sv4cpio");
        FILE_TYPE.put(".sv4crc" , "application/x-sv4crc");
        FILE_TYPE.put(".svg" , "image/svg+xml");
        FILE_TYPE.put(".svgz" , "image/svg+xml-compressed");
        FILE_TYPE.put(".swf" , "application/x-shockwave-flash");
        FILE_TYPE.put(".sxc" , "application/vnd.sun.xml.calc");
        FILE_TYPE.put(".sxd" , "application/vnd.sun.xml.draw");
        FILE_TYPE.put(".sxg" , "application/vnd.sun.xml.writer.global");
        FILE_TYPE.put(".sxi" , "application/vnd.sun.xml.impress");
        FILE_TYPE.put(".sxm" , "application/vnd.sun.xml.math");
        FILE_TYPE.put(".sxw" , "application/vnd.sun.xml.writer");
        FILE_TYPE.put(".sylk" , "text/spreadsheet");
        FILE_TYPE.put(".t" , "text/troff");
        FILE_TYPE.put(".t2t" , "text/x-txt2tags");
        FILE_TYPE.put(".tar" , "application/x-tar");
        FILE_TYPE.put(".tar.bz" , "application/x-bzip-compressed-tar");
        FILE_TYPE.put(".tar.bz2" , "application/x-bzip-compressed-tar");
        FILE_TYPE.put(".tar.gz" , "application/x-compressed-tar");
        FILE_TYPE.put(".tar.lzma" , "application/x-lzma-compressed-tar");
        FILE_TYPE.put(".tar.lzo" , "application/x-tzo");
        FILE_TYPE.put(".tar.xz" , "application/x-xz-compressed-tar");
        FILE_TYPE.put(".tar.z" , "application/x-tarz");
        FILE_TYPE.put(".tbz" , "application/x-bzip-compressed-tar");
        FILE_TYPE.put(".tbz2" , "application/x-bzip-compressed-tar");
        FILE_TYPE.put(".tcl" , "text/x-tcl");
        FILE_TYPE.put(".tex" , "text/x-tex");
        FILE_TYPE.put(".texi" , "text/x-texinfo");
        FILE_TYPE.put(".texinfo" , "text/x-texinfo");
        FILE_TYPE.put(".tga" , "image/x-tga");
        FILE_TYPE.put(".tgz" , "application/x-compressed-tar");
        FILE_TYPE.put(".theme" , "application/x-theme");
        FILE_TYPE.put(".themepack" , "application/x-windows-themepack");
        FILE_TYPE.put(".tif" , "image/tiff");
        FILE_TYPE.put(".tiff" , "image/tiff");
        FILE_TYPE.put(".tk" , "text/x-tcl");
        FILE_TYPE.put(".tlz" , "application/x-lzma-compressed-tar");
        FILE_TYPE.put(".tnef" , "application/vnd.ms-tnef");
        FILE_TYPE.put(".tnf" , "application/vnd.ms-tnef");
        FILE_TYPE.put(".toc" , "application/x-cdrdao-toc");
        FILE_TYPE.put(".torrent" , "application/x-bittorrent");
        FILE_TYPE.put(".tpic" , "image/x-tga");
        FILE_TYPE.put(".tr" , "text/troff");
        FILE_TYPE.put(".ts" , "application/x-linguist");
        FILE_TYPE.put(".tsv" , "text/tab-separated-values");
        FILE_TYPE.put(".tta" , "audio/x-tta");
        FILE_TYPE.put(".ttc" , "application/x-font-ttf");
        FILE_TYPE.put(".ttf" , "application/x-font-ttf");
        FILE_TYPE.put(".ttx" , "application/x-font-ttx");
        FILE_TYPE.put(".txt" , "text/plain");
        FILE_TYPE.put(".txz" , "application/x-xz-compressed-tar");
        FILE_TYPE.put(".tzo" , "application/x-tzo");
        FILE_TYPE.put(".ufraw" , "application/x-ufraw");
        FILE_TYPE.put(".ui" , "application/x-designer");
        FILE_TYPE.put(".uil" , "text/x-uil");
        FILE_TYPE.put(".ult" , "audio/x-mod");
        FILE_TYPE.put(".uni" , "audio/x-mod");
        FILE_TYPE.put(".uri" , "text/x-uri");
        FILE_TYPE.put(".url" , "text/x-uri");
        FILE_TYPE.put(".ustar" , "application/x-ustar");
        FILE_TYPE.put(".vala" , "text/x-vala");
        FILE_TYPE.put(".vapi" , "text/x-vala");
        FILE_TYPE.put(".vcf" , "text/directory");
        FILE_TYPE.put(".vcs" , "text/calendar");
        FILE_TYPE.put(".vct" , "text/directory");
        FILE_TYPE.put(".vda" , "image/x-tga");
        FILE_TYPE.put(".vhd" , "text/x-vhdl");
        FILE_TYPE.put(".vhdl" , "text/x-vhdl");
        FILE_TYPE.put(".viv" , "video/vivo");
        FILE_TYPE.put(".vivo" , "video/vivo");
        FILE_TYPE.put(".vlc" , "audio/x-mpegurl");
        FILE_TYPE.put(".vob" , "video/mpeg");
        FILE_TYPE.put(".voc" , "audio/x-voc");
        FILE_TYPE.put(".vor" , "application/vnd.stardivision.writer");
        FILE_TYPE.put(".vst" , "image/x-tga");
        FILE_TYPE.put(".wav" , "audio/x-wav");
        FILE_TYPE.put(".wax" , "audio/x-ms-asx");
        FILE_TYPE.put(".wb1" , "application/x-quattropro");
        FILE_TYPE.put(".wb2" , "application/x-quattropro");
        FILE_TYPE.put(".wb3" , "application/x-quattropro");
        FILE_TYPE.put(".wbmp" , "image/vnd.wap.wbmp");
        FILE_TYPE.put(".wcm" , "application/vnd.ms-works");
        FILE_TYPE.put(".wdb" , "application/vnd.ms-works");
        FILE_TYPE.put(".webm" , "video/webm");
        FILE_TYPE.put(".wk1" , "application/vnd.lotus-1-2-3");
        FILE_TYPE.put(".wk3" , "application/vnd.lotus-1-2-3");
        FILE_TYPE.put(".wk4" , "application/vnd.lotus-1-2-3");
        FILE_TYPE.put(".wks" , "application/vnd.ms-works");
        FILE_TYPE.put(".wma" , "audio/x-ms-wma");
        FILE_TYPE.put(".wmf" , "image/x-wmf");
        FILE_TYPE.put(".wml" , "text/vnd.wap.wml");
        FILE_TYPE.put(".wmls" , "text/vnd.wap.wmlscript");
        FILE_TYPE.put(".wmv" , "video/x-ms-wmv");
        FILE_TYPE.put(".wmx" , "audio/x-ms-asx");
        FILE_TYPE.put(".wp" , "application/vnd.wordperfect");
        FILE_TYPE.put(".wp4" , "application/vnd.wordperfect");
        FILE_TYPE.put(".wp5" , "application/vnd.wordperfect");
        FILE_TYPE.put(".wp6" , "application/vnd.wordperfect");
        FILE_TYPE.put(".wpd" , "application/vnd.wordperfect");
        FILE_TYPE.put(".wpg" , "application/x-wpg");
        FILE_TYPE.put(".wpl" , "application/vnd.ms-wpl");
        FILE_TYPE.put(".wpp" , "application/vnd.wordperfect");
        FILE_TYPE.put(".wps" , "application/vnd.ms-works");
        FILE_TYPE.put(".wri" , "application/x-mswrite");
        FILE_TYPE.put(".wrl" , "model/vrml");
        FILE_TYPE.put(".wv" , "audio/x-wavpack");
        FILE_TYPE.put(".wvc" , "audio/x-wavpack-correction");
        FILE_TYPE.put(".wvp" , "audio/x-wavpack");
        FILE_TYPE.put(".wvx" , "audio/x-ms-asx");
        FILE_TYPE.put(".x3f" , "image/x-sigma-x3f");
        FILE_TYPE.put(".xac" , "application/x-gnucash");
        FILE_TYPE.put(".xbel" , "application/x-xbel");
        FILE_TYPE.put(".xbl" , "application/xml");
        FILE_TYPE.put(".xbm" , "image/x-xbitmap");
        FILE_TYPE.put(".xcf" , "image/x-xcf");
        FILE_TYPE.put(".xcf.bz2" , "image/x-compressed-xcf");
        FILE_TYPE.put(".xcf.gz" , "image/x-compressed-xcf");
        FILE_TYPE.put(".xhtml" , "application/xhtml+xml");
        FILE_TYPE.put(".xi" , "audio/x-xi");
        FILE_TYPE.put(".xla" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xlc" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xld" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xlf" , "application/x-xliff");
        FILE_TYPE.put(".xliff" , "application/x-xliff");
        FILE_TYPE.put(".xll" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xlm" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xls" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xlsm" , "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        FILE_TYPE.put(".xlsx" , "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        FILE_TYPE.put(".xlt" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xlw" , "application/vnd.ms-excel");
        FILE_TYPE.put(".xm" , "audio/x-xm");
        FILE_TYPE.put(".xmf" , "audio/x-xmf");
        FILE_TYPE.put(".xmi" , "text/x-xmi");
        FILE_TYPE.put(".xml" , "application/xml");
        FILE_TYPE.put(".xpm" , "image/x-xpixmap");
        FILE_TYPE.put(".xps" , "application/vnd.ms-xpsdocument");
        FILE_TYPE.put(".xsl" , "application/xml");
        FILE_TYPE.put(".xslfo" , "text/x-xslfo");
        FILE_TYPE.put(".xslt" , "application/xml");
        FILE_TYPE.put(".xspf" , "application/xspf+xml");
        FILE_TYPE.put(".xul" , "application/vnd.mozilla.xul+xml");
        FILE_TYPE.put(".xwd" , "image/x-xwindowdump");
        FILE_TYPE.put(".xyz" , "chemical/x-pdb");
        FILE_TYPE.put(".xz" , "application/x-xz");
        FILE_TYPE.put(".w2p" , "application/w2p");
        FILE_TYPE.put(".z" , "application/x-compress");
        FILE_TYPE.put(".zabw" , "application/x-abiword");
        FILE_TYPE.put(".zip" , "application/zip");
    }

    /**
     * 读取zip文件内容为MultipartFile
     *
     * @param zipFile
     * @return
     */
    public static List<ZipInnerFile> unzipToMultipartFile(MultipartFile zipFile) {
        List<ZipInnerFile> zipInnerFiles = new ArrayList<>();
        if (zipFile == null) {
            throw new RuntimeException("文件不能为空!");
        }
        String originalFilename = zipFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException("文件名为空!");
        }
        if (!originalFilename.endsWith(".zip")) {
            throw new RuntimeException("请传入Zip压缩文件");
        }
        String zipFileName = originalFilename.replace(".zip", "");
        String zipFileNamePath = zipFileName +"/";
        // 读取Zip文件
        try (ZipInputStream zipIn = new ZipInputStream(zipFile.getInputStream(), Charset.forName("GBK"));) {
            ZipEntry zipEntry = null;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();) {
                    ZipInnerFile zipInnerFile = new ZipInnerFile();
                    zipInnerFile.setZipName(zipFileName);

                    IOUtils.copy(zipIn, byteOut);
                    InputStream inputStream = new ByteArrayInputStream(byteOut.toByteArray());
                    String fileName = zipEntry.getName();
                    zipInnerFile.setFullFilePath(fileName);
                    if (fileName.contains(directoryPath) || fileName.contains(directory)) {
                        // 文件根目录过滤
                        if (directory.equalsIgnoreCase(fileName)) {
                            continue;
                        }
                        if (fileName.contains(directoryPath)) {
                            fileName = fileName.replace(directoryPath, "");
                        }
                    }
                    fileName = fileName.replace(zipFileNamePath,"");
                    zipInnerFile.setFilePath(fileName);
                    List<String> stringList = Arrays.asList(fileName.split("/"));
                    fileName = stringList.get(stringList.size() - 1);
                    zipInnerFile.setFileName(fileName);
                    if (!fileName.contains(".")) {
                        continue;
                    }
                    MultipartFile multipartFile = FileUtility.getMultipartFile(inputStream, fileName);
                    zipInnerFile.setFile(multipartFile);
                    zipInnerFiles.add(zipInnerFile);
                }
            }
        } catch (Exception e) {
            log.error("读取Zip文件异常!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            throw new RuntimeException("读取Zip文件异常!" + ExceptionUtil.getSimpleMessage(e));
        }
        return zipInnerFiles;
    }

    /**
     * 获取封装得MultipartFile
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return MultipartFile
     */
    public static MultipartFile getMultipartFile(InputStream inputStream, String fileName) {
        FileItem fileItem = createFileItem(inputStream, fileName);
        //CommonsMultipartFile是feign对multipartFile的封装，但是要FileItem类对象
        return new CommonsMultipartFile(fileItem);
    }


    /**
     * FileItem类对象创建
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return FileItem
     */
    public static FileItem createFileItem(InputStream inputStream, String fileName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "file";
        String end = fileName.substring(fileName.lastIndexOf("."));
        String contentType = FILE_TYPE.get(end);
        FileItem item = factory.createItem(textFieldName, contentType, false, fileName);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        OutputStream os = null;
        //使用输出流输出输入流的字节
        try {
            os = item.getOutputStream();
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        } catch (IOException e) {
            log.error("Stream copy exception", e);
            throw new IllegalArgumentException("文件上传失败");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("Stream close exception", e);

                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Stream close exception", e);
                }
            }
        }

        return item;
    }


}
