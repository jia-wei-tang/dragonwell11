# This file identifies the root of the test-suite hierarchy.
# It also contains test-suite configuration information.

# The list of keywords supported in the entire test suite.  The
# "intermittent" keyword marks tests known to fail intermittently.
# The "randomness" keyword marks tests using randomness with test
# cases differing from run to run. (A test using a fixed random seed
# would not count as "randomness" by this definition.) Extra care
# should be taken to handle test failures of intermittent or
# randomness tests.
#
# A "headful" test requires a graphical environment to meaningfully
# run. Tests that are not headful are "headless".
# A test flagged with key sound needs audio devices on the system, this
# may be accompanied by the headful keyword since audio device access 
# is often linked to access to desktop resources and headful systems are
# also more likely to have audio devices (ie meaning both input and output)
# A test flagged with key "printer" requires a printer to succeed, else
# throws a PrinterException or the like.
# A test flagged with cgroups uses cgroups.

keys=2d dnd headful sound i18n intermittent printer randomness jfr cgroups

# Tests that must run in othervm mode
othervm.dirs=java/awt java/beans javax/accessibility javax/imageio javax/sound javax/swing javax/print \
com/apple/laf com/sun/java/accessibility com/sun/java/swing sanity/client demo/jfc \
javax/management com/sun/awt sun/awt sun/java2d javax/xml/jaxp/testng/validation java/lang/ProcessHandle

# Tests that cannot run concurrently
exclusiveAccess.dirs=java/rmi/Naming java/util/prefs sun/management/jmxremote sun/tools/jstatd \
sun/security/mscapi java/util/stream java/util/Arrays/largeMemory \
java/util/BitSet/stream javax/rmi java/net/httpclient/websocket \
sanity/client sun/tools/jhsdb \
com/alibaba/wisp/exclusive com/alibaba/wisp2/exclusive jdk/crac/java

# Group definitions
groups=TEST.groups

# Allow querying of various System properties in @requires clauses
#
# Source files for classes that will be used at the beginning of each test suite run,
# to determine additional characteristics of the system for use with the @requires tag.
# Note: compiled bootlibs code will be located in the folder 'bootClasses'
requires.extraPropDefns = ../jtreg-ext/requires/VMProps.java
requires.extraPropDefns.bootlibs = ../lib/sun \
    ../lib/jdk/test/lib/Platform.java \
    ../lib/jdk/test/lib/Container.java
requires.extraPropDefns.vmOpts = -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -Xbootclasspath/a:bootClasses
requires.properties= \
    sun.arch.data.model \
    java.runtime.name \
    vm.gc.G1 \
    vm.gc.Z \
    vm.gc.Shenandoah \
    vm.graal.enabled \
    vm.compiler1.enabled \
    vm.compiler2.enabled \
    vm.cds \
    vm.musl \
    vm.debug \
    vm.hasSA \
    vm.hasJFR \
    docker.support \
    release.implementor

# Minimum jtreg version
requiredVersion=7.3.1+1

# Path to libraries in the topmost test directory. This is needed so @library
# does not need ../../ notation to reach them
external.lib.roots = ../../

# Use new module options
useNewOptions=true

# Use --patch-module instead of -Xmodule:
useNewPatchModule=true
