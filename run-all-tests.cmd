@echo off
rem Lance les tests Maven de tous les micro-services et ecrit le resultat dans test-results.log
cd /d "%~dp0"
set "LOG=%~dp0test-results.log"
echo Tests started %date% %time% > "%LOG%"
for %%s in (Discovery-Service Customer-Service EBank-Service Gateway-Service AI-Agent-Service Discord-Bot-Service) do (
  echo ===== %%s ===== >> "%LOG%"
  pushd "%%s\%%s"
  call mvnw.cmd -B test >> "%LOG%" 2>&1
  if errorlevel 1 (echo RESULT %%s FAILED >> "%LOG%") else (echo RESULT %%s OK >> "%LOG%")
  popd
)
echo ALL DONE %date% %time% >> "%LOG%"
