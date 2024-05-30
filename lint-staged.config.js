// https://github.com/lint-staged/lint-staged?tab=readme-ov-file#using-js-configuration-files
// lint-staged.config.js
// import micromatch from 'micromatch'

// export default (allStagedFiles) => {
//     const javaFiles = micromatch(allStagedFiles, ['**/*.{java,kt}'])
//     if (javaFiles.length === 0) {
//         return `printf '%s\n' "未检测到 java 代码修改" >&2`
//     }
//     return `./mvnw spotless:apply -DspotlessFiles=${javaFiles.join(',')}`
//     // return `./husky/spotless-apply "${javaFiles.join(',')}"`
// }

module.exports = {
  '**/*.{java,tk}': (filenames) =>
    `./mvnw spotless:apply -DspotlessFiles=${filenames.join(',')}`,
  '**/pom.xml': (filenames) =>
    `./mvnw spotless:apply -DspotlessFiles=${filenames.join(',')}`,
  '**/*.{son,js,yml,yaml}': (filenames) =>
    `prettier --write --ignore-unknown ${filenames.join(' ')}`,
}
