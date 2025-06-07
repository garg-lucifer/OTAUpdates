import 'react-native-url-polyfill/auto';
import React, {useEffect, useState} from 'react';
import {createClient} from '@supabase/supabase-js';
import {View, Text, StyleSheet, FlatList, TouchableOpacity} from 'react-native';
import RNFS from 'react-native-fs';

export const supabase = createClient('', '');

const currentVersion = '3.0.0';

function App(): React.JSX.Element {
  const [versions, setVersions] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchVersions = async () => {
      const {data, error} = await supabase.storage.from('versions').list();
      console.log('DATA:', JSON.stringify(data, null, 2));
      console.log('ERROR:', error);
      if (error) {
        console.error('Error fetching versions:', error.message);
        setLoading(false);
        return;
      }

      const versionNames = data?.map(file => file.name) || [];
      setVersions(versionNames);
      setLoading(false);
    };

    fetchVersions();
  }, []);

  const handleVersionClick = async (version: string) => {
    try {
      // Step 1: Get a signed URL or public URL
      const {data, error} = await supabase.storage
        .from('versions')
        .createSignedUrl(version, 60); // valid for 60 secs

      if (error || !data?.signedUrl) {
        console.error('Failed to get signed URL:', error?.message);
        return;
      }

      const fileUrl = data.signedUrl;
      const localPath = `${RNFS.DocumentDirectoryPath}/${version}`;

      // Step 2: Download the file
      const downloadResult = await RNFS.downloadFile({
        fromUrl: fileUrl,
        toFile: localPath,
      }).promise;

      if (downloadResult.statusCode === 200) {
        console.log(`Downloaded ${version} to: ${localPath}`);
        const path = `${RNFS.DocumentDirectoryPath}/version.txt`;
        await RNFS.writeFile(path, version, 'utf8');
      } else {
        console.error(
          'Download failed with status code:',
          downloadResult.statusCode,
        );
      }
    } catch (err) {
      console.error('Error downloading version file:', err);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.versionLabel}>App Version: {currentVersion}</Text>
        <Text style={styles.heading}>Select a Version to Switch</Text>
      </View>

      {loading ? (
        <Text style={styles.loading}>Fetching versions...</Text>
      ) : versions.length === 0 ? (
        <Text style={styles.noVersions}>No versions found.</Text>
      ) : (
        <FlatList
          data={versions}
          keyExtractor={item => item}
          contentContainerStyle={styles.grid}
          numColumns={2}
          columnWrapperStyle={styles.row}
          renderItem={({item}) => (
            <TouchableOpacity
              style={styles.card}
              onPress={() => handleVersionClick(item)}>
              <Text style={styles.cardVersion}>{item}</Text>
              <Text style={styles.cardAction}>Tap to switch</Text>
            </TouchableOpacity>
          )}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#eef1f6',
    padding: 16,
  },
  header: {
    marginBottom: 24,
    alignItems: 'center',
  },
  versionLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#4a5568',
  },
  heading: {
    fontSize: 20,
    fontWeight: '700',
    color: '#2d3748',
    marginTop: 4,
  },
  loading: {
    textAlign: 'center',
    fontSize: 16,
    color: '#718096',
  },
  noVersions: {
    textAlign: 'center',
    fontSize: 16,
    color: '#e53e3e',
  },
  grid: {
    paddingBottom: 24,
  },
  row: {
    justifyContent: 'space-between',
  },
  card: {
    flex: 1,
    backgroundColor: '#fff',
    borderRadius: 12,
    margin: 8,
    paddingVertical: 24,
    paddingHorizontal: 12,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 3,
  },
  cardVersion: {
    fontSize: 18,
    fontWeight: '700',
    color: '#2b6cb0',
  },
  cardAction: {
    fontSize: 13,
    color: '#718096',
    marginTop: 6,
  },
});

export default App;
