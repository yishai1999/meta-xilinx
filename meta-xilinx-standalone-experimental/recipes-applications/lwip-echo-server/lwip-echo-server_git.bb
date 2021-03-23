inherit esw deploy python3native

ESW_COMPONENT_SRC = "/lib/sw_apps/lwip_echo_server/src/"

DEPENDS += "libxil lwip xiltimer"

do_configure_prepend() {
    cd ${S}
    lopper.py ${DTS_FILE} -- baremetallinker_xlnx.py ${ESW_MACHINE} ${S}/${ESW_COMPONENT_SRC}
    install -m 0755 memory.ld ${S}/${ESW_COMPONENT_SRC}/
    install -m 0755 *.cmake ${S}/${ESW_COMPONENT_SRC}/
}

do_generate_app_data() {
    # This script should also not rely on relative paths and such
    cd ${S}
    lopper.py ${DTS_FILE} -- bmcmake_metadata_xlnx.py ${ESW_MACHINE} ${S}/${ESW_COMPONENT_SRC} hwcmake_metadata ${S}
    install -m 0755 *.cmake ${S}/${ESW_COMPONENT_SRC}/
}
addtask do_generate_app_data before do_configure after do_prepare_recipe_sysroot
do_prepare_recipe_sysroot[rdeptask] = "do_unpack"

do_install() {
    install -d ${D}/${base_libdir}/firmware
    # Note that we have to make the ELF executable for it to be stripped
    install -m 0755  ${B}/lwip_echo* ${D}/${base_libdir}/firmware
}

LWIP_ECHO_BASE_NAME ?= "${BPN}-${PKGE}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
LWIP_ECHO_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy() {

    # We need to deploy the stripped elf, hence why not doing it from ${D}
    install -Dm 0644 ${WORKDIR}/package/${base_libdir}/firmware/lwip_echo_server.elf ${DEPLOYDIR}/${LWIP_ECHO_BASE_NAME}.elf
    ln -sf ${LWIP_ECHO_BASE_NAME}.elf ${DEPLOYDIR}/${BPN}-${MACHINE}.elf
    ${OBJCOPY} -O binary ${WORKDIR}/package/${base_libdir}/firmware/lwip_echo_server.elf ${WORKDIR}/package/${base_libdir}/firmware/lwip_echo.bin
    install -m 0644 ${WORKDIR}/package/${base_libdir}/firmware/lwip_echo.bin ${DEPLOYDIR}/${LWIP_ECHO_BASE_NAME}.bin
    ln -sf ${LWIP_ECHO_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
}

addtask deploy before do_build after do_package

FILES_${PN} = "${base_libdir}/firmware/lwip_echo*"
