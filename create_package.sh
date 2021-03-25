
# Git State
UPSTREAM='@{u}'
LOCAL=$(git rev-parse HEAD)
REMOTE=$(git rev-parse "$UPSTREAM")
BASE=$(git merge-base HEAD "$UPSTREAM")


SOURCE_BASE=.
RELEASE_NAME=juno_setup_files.`date +'%Y-%m-%d'`
PACKAGE_LOCATION=/tmp
PACKAGE_BASE=$PACKAGE_LOCATION/$RELEASE_NAME
ARCHIVE_NAME=$SOURCE_BASE/$RELEASE_NAME.tar.gz
OSCAR_DIR=juno
OSCAR_BASE=$PACKAGE_BASE/$OSCAR_DIR
WAR_FILE_SOURCE=$SOURCE_BASE/target/oscar-14.0.0-SNAPSHOT.war
WAR_FILE_DEST=$OSCAR_BASE/oscar.war
DB_DIR_SOURCE=$SOURCE_BASE/database/
DB_DIR_DEST=$OSCAR_BASE/
DB_SCHEMA_SOURCE=$SOURCE_BASE/database/mysql/oscar_15.creation_db.*.sql
DB_SCHEMA_DEST=$OSCAR_BASE/
MISC_FILES_SOURCE=$SOURCE_BASE/package_files/*
MISC_FILES_DEST=$OSCAR_BASE/

confirm() {
    # call with a prompt string or use a default
    read -r -p "${1:-Are you sure? [y/N]} " response
    case "$response" in
        [yY][eE][sS]|[yY]) 
            true
            ;;
        *)
            false
            ;;
    esac
}


echo ""
echo "Creating release package"
echo "------------------------"
echo ""


# Check to see what state the repo is in.  Continue if it matches remote.
# Code pulled from http://stackoverflow.com/questions/3258243/check-if-pull-needed-in-git
if [ $LOCAL = $REMOTE ]; then
	echo "Local checkout matches remote"
elif [ $LOCAL = $BASE ]; then
	! confirm "There are remote git changes (requires pull).  Are you sure you want to continue? [y/N]" && exit 1
elif [ $REMOTE = $BASE ]; then
	! confirm "There are local git changes (requires push).  Are you sure you want to continue? [y/N]" && exit 1
else
	! confirm "Remote repo has diverged.  Are you sure you want to continue? [y/N]" && exit 1
fi


if [[ -d "$PACKAGE_BASE" ]]; then
	echo "Found old package directory, deleting..."
	rm -rf $PACKAGE_BASE
fi

mkdir -p $OSCAR_BASE


echo "Copy war file"
if [[ ! -f "$WAR_FILE_SOURCE" ]]; then
	echo "No war file found ($WAR_FILE_SOURCE).  You probably need to build."
	exit 1
fi
cp $WAR_FILE_SOURCE $WAR_FILE_DEST


echo "Copy database directory"
if [[ ! -d "$DB_DIR_SOURCE" ]]; then
	echo "No database directory found ($DB_DIR_SOURCE)."
	exit 1
fi
cp -r $DB_DIR_SOURCE $DB_DIR_DEST


echo "Copy database root schema"
if ! stat --printf='' $DB_SCHEMA_SOURCE 2>/dev/null; then
	echo "No database schema found ($DB_SCHEMA_SOURCE)."
	exit 1
fi
cp $DB_SCHEMA_SOURCE $DB_SCHEMA_DEST


echo "Copy package files"
if ! stat --printf='' $MISC_FILES_SOURCE 2>/dev/null; then
	echo "No database schema found ($MISC_FILES_SOURCE)."
	exit 1
fi
cp -r $MISC_FILES_SOURCE $MISC_FILES_DEST


echo "Create the archive"
tar czf $ARCHIVE_NAME --owner=oscarhost --group=oscarhost -C $PACKAGE_LOCATION $RELEASE_NAME

echo "Cleaning up..."
rm -rf $PACKAGE_BASE

exit 0
