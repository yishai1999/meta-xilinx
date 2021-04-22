# Only should be used for development
DEFAULT_PREFERENCE = "-1"

require fsbl-firmware.inc

FILESPATH .= ":${FILE_DIRNAME}/embeddedsw"

SRC_URI += " \
            file://zynqmp_pmufw-fixup.patch \
            file://makefile-skip-copy_bsp.sh.patch \
            file://fsbl-fixups.patch \
           "

# This version does not build for zynq
COMPATIBLE_MACHINE_zynq = "none"
