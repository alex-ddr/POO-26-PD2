JAVAC = javac
JAVA = java
SRC_DIR = src
BUILD_DIR = out
MAIN_CLASS = MainFILEY

SOURCES = $(wildcard $(SRC_DIR)/datos/*.java) \
          $(wildcard $(SRC_DIR)/notificaciones/*.java) \
          $(wildcard $(SRC_DIR)/logica/*.java) \
          $(SRC_DIR)/$(MAIN_CLASS).java

.PHONY: all compile run clean

all: run

compile:
	$(JAVAC) -d $(BUILD_DIR) $(SOURCES)

run: compile
	$(JAVA) -cp $(BUILD_DIR) $(MAIN_CLASS)

clean:
	if exist "$(BUILD_DIR)" rmdir /s /q "$(BUILD_DIR)"
