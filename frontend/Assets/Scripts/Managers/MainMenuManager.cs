using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class MainMenuManager : MonoBehaviour
{
    [SerializeField] private Button startButton;
    [SerializeField] private Button exitButton;
    [SerializeField] private Text statusText;

    void Start()
    {
        startButton.onClick.AddListener(OnStartClicked);
        exitButton.onClick.AddListener(() => Application.Quit());
    }

    private void OnStartClicked()
    {
        startButton.interactable = false;
        SetStatus("Starting run...");

        StartCoroutine(ApiClient.Instance.Post<StartRunResponse>("/runs", null,
            response =>
            {
                GameManager.Instance.CurrentRunId = response.runId;
                GameManager.Instance.CurrentRun = response;
                SceneManager.LoadScene("MapScene");
            },
            error =>
            {
                SetStatus("Could not connect to server.\nMake sure the backend is running.");
                startButton.interactable = true;
            }
        ));
    }

    private void SetStatus(string msg)
    {
        if (statusText != null) statusText.text = msg;
    }
}
